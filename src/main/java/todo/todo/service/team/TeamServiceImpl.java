package todo.todo.service.team;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import todo.todo.dto.request.team.AddTeamBaseReq;
import todo.todo.dto.response.team.TeamDetailRes;
import todo.todo.dto.response.teamMember.TeamMemberRes;
import todo.todo.dto.response.user.UserDetailRes;
import todo.todo.entity.team_member.TeamMember;
import todo.todo.entity.teams.Team;
import todo.todo.entity.user.User;
import todo.todo.repository.teamMember.TeamMemberRepository;
import todo.todo.repository.teams.TeamRepository;
import todo.todo.repository.user.UserRepository;
import todo.todo.service.BaseService;

@Service
public class TeamServiceImpl extends BaseService implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamServiceImpl(TeamMemberRepository teamMemberRepository, TeamRepository teamRepository,
            UserRepository userRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void AssignJob() {
        throw new UnsupportedOperationException("Unimplemented method 'AssignJob'");
    }

    @Override
    public TeamDetailRes CreateTeam(AddTeamBaseReq request, int currentUserId) {
        Team team = new Team();
        team.setName(request.getName());
        User currentUser = teamRepository.findByOwnerId(currentUserId);
        team.setOwner(currentUser);
        team.setDescription(request.getDescription());
        team.setCreatedAt(new Date());
        team.setUpdatedAt(new Date());
        teamRepository.save(team);

        User owner = userRepository.findById(currentUserId);

        TeamMember tm = new TeamMember();
        tm.setTeam(team);
        tm.setUser(owner);
        tm.setRole(TeamMember.Role.owner);
        tm.setCreatedAt(new Date());
        tm.setUpdatedAt(new Date());
        teamMemberRepository.save(tm);

        return TeamDetailRes.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .owner(
                        UserDetailRes.builder()
                                .id(team.getOwner().getId())
                                .fullname(team.getOwner().getFullName())
                                .email(team.getOwner().getEmail())
                                .avatarUrl(team.getOwner().getAvatarUrl())
                                .build())
                .members(List.of())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    @Override
    public TeamDetailRes UpdateTeam(AddTeamBaseReq request, int foundId, int currentUserId) {
        Team team = teamRepository.findById(foundId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + foundId));

        if (request.getName() != null && !request.getName().isEmpty()) {
            team.setName(request.getName());
        }
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            team.setDescription(request.getDescription());
        }

        teamRepository.save(team);

        List<TeamMember> members = teamMemberRepository.findActiveByTeam(team.getId());
        List<TeamMemberRes> memberDtos = members.stream()
                .map(m -> TeamMemberRes.builder()
                        .id(m.getUser().getId())
                        .fullName(m.getUser().getFullName())
                        .email(m.getUser().getEmail())
                        .avatarUrl(m.getUser().getAvatarUrl())
                        .role(m.getRole().name())
                        .joinedAt(m.getCreatedAt())
                        .build())
                .toList();

        User owner = team.getOwner();
        return TeamDetailRes.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .owner(UserDetailRes.builder()
                        .id(owner != null ? owner.getId() : 0)
                        .fullname(owner != null ? owner.getFullName() : null)
                        .email(owner != null ? owner.getEmail() : null)
                        .avatarUrl(owner != null ? owner.getAvatarUrl() : null)
                        .build())
                .members(memberDtos)
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    @Override
    public TeamDetailRes DeleteTeam(int teamId, int currentUserId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        if (team.getOwner() == null || team.getOwner().getId() != currentUserId) {
            throw new RuntimeException("Only the owner can delete this team");
        }

        Date now = new Date();

        // Team
        team.setDeleted(true);
        team.setUpdatedAt(now);
        teamRepository.save(team);

        List<TeamMember> members = teamMemberRepository.findByTeam_IdAndDeletedIsNull(teamId);
        for (TeamMember m : members) {
            m.setDeleted(true);
            m.setUpdatedAt(now);
        }
        teamMemberRepository.saveAll(members);

        return TeamDetailRes.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .owner(
                        UserDetailRes.builder()
                                .id(team.getOwner().getId())
                                .fullname(team.getOwner().getFullName())
                                .email(team.getOwner().getEmail())
                                .avatarUrl(team.getOwner().getAvatarUrl())
                                .build())
                .members(List.of())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    @Override
    public TeamMemberRes addMemberToTeam(int teamId, int userId, int currentUserId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        TeamMember currentUserMembership = teamMemberRepository.findByUserIdAndTeamId(currentUserId, teamId);

        if (currentUserMembership == null) {
            throw new RuntimeException("You are not in this team");
        }

        if (currentUserMembership.getRole() == TeamMember.Role.member) {
            throw new RuntimeException("You don't have permission to add new member");
        }

        User newUser = userRepository.findById(userId);
        if (newUser == null) {
            throw new RuntimeException("User not found: " + userId);
        }

        TeamMember newMember = new TeamMember();
        newMember.setTeam(team);
        newMember.setUser(newUser);
        newMember.setRole(TeamMember.Role.member);
        newMember.setCreatedAt(new Date());
        newMember.setUpdatedAt(new Date());
        newMember.setDeleted(false);

        TeamMemberRes t = new TeamMemberRes();

        teamMemberRepository.save(newMember);

        return t.builder()
                .id(newMember.getId())
                .fullName(newMember.getUser().getFullName())
                .email(newMember.getUser().getEmail())
                .avatarUrl(newMember.getUser().getAvatarUrl())
                .build();
    }

    @Override
    public TeamMemberRes updateMemberRole(int teamId, int userId, String newRole) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        TeamMember member = teamMemberRepository.findByUserIdAndTeamId(userId, teamId);

        if (member.getTeam() == null || Boolean.TRUE.equals(member.isDeleted())) {
            throw new RuntimeException("Member not found in this team");
        }

        TeamMember.Role roleEnum;
        try {
            roleEnum = TeamMember.Role.valueOf(newRole.trim().toLowerCase());
        } catch (IllegalArgumentException ex) {
            roleEnum = TeamMember.Role.valueOf(newRole.trim().toUpperCase());
        }

        // if (member.getRole() == TeamMember.Role.owner && roleEnum !=
        // TeamMember.Role.owner) {
        // throw new RuntimeException("Cannot change role of team owner");
        // }

        member.setRole(roleEnum);
        member.setUpdatedAt(new Date());
        teamMemberRepository.save(member);

        User u = member.getUser();

        return TeamMemberRes.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .avatarUrl(u.getAvatarUrl())
                .role(roleEnum.name())
                .joinedAt(member.getCreatedAt())
                .build();
    }

    @Override
    public TeamDetailRes getTeamDetail(int teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        List<TeamMember> members = teamMemberRepository.findActiveByTeam(teamId);

        List<TeamMemberRes> memberDtos = members.stream()
                .map(m -> TeamMemberRes.builder()
                        .id(m.getUser().getId())
                        .fullName(m.getUser().getFullName())
                        .email(m.getUser().getEmail())
                        .avatarUrl(m.getUser().getAvatarUrl())
                        .role(m.getRole().name())
                        .build())
                .toList();

        User owner = team.getOwner();

        return TeamDetailRes.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .owner(UserDetailRes.builder()
                        .id(owner != null ? owner.getId() : 0)
                        .fullname(owner != null ? owner.getFullName() : null)
                        .email(owner != null ? owner.getEmail() : null)
                        .avatarUrl(owner != null ? owner.getAvatarUrl() : null)
                        .build())
                .members(memberDtos)
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    @Override
    public TeamMemberRes deleteMemberFromTeam(int teamId, int userId, int currentUserId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        TeamMember currentUserMembership = teamMemberRepository.findByUserIdAndTeamId(currentUserId, teamId);
        System.out.println(currentUserMembership.getId());
        System.out.println(currentUserMembership.getRole());
        System.out.println(currentUserMembership.getTeam());
        if (currentUserMembership == null || currentUserMembership.isDeleted()) {
            throw new RuntimeException("You are not in this team");
        }
        if (currentUserMembership.getRole() == TeamMember.Role.member) {
            throw new RuntimeException("You don't have permission to remove members");
        }

        TeamMember target = teamMemberRepository.findByUserIdAndTeamId(userId, teamId);
        if (target == null || Boolean.TRUE.equals(target.isDeleted())) {
            throw new RuntimeException("Member not found in this team");
        }

        // Không cho xóa Owner
        if (target.getRole() == TeamMember.Role.owner) {
            throw new RuntimeException("Cannot remove the team owner");
        }

        target.setDeleted(true);
        target.setUpdatedAt(new Date());
        teamMemberRepository.save(target);

        return TeamMemberRes.builder()
                .id(target.getId())
                .fullName(target.getUser().getFullName())
                .email(target.getUser().getEmail())
                .avatarUrl(target.getUser().getAvatarUrl())
                .role(target.getRole().name())
                .build();
    }

    @Override
    public TeamDetailRes getTeamByUser(int userId) {

        // Lấy tất cả team mà user thuộc (owner hoặc member)
        List<Team> teams = teamRepository.getTeams(userId);

        if (teams == null || teams.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy team nào của user " + userId);
        }

        // Chọn 1 team để trả về (ở đây lấy team đầu tiên)
        // Nếu muốn ưu tiên team mà user là owner thì có thể filter trước.
        Team team = teams.get(0);

        // BUILD DTO KHÔNG DÙNG MAPPER
        TeamDetailRes dto = new TeamDetailRes();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setCreatedAt(team.getCreatedAt());
        dto.setUpdatedAt(team.getUpdatedAt());

        // Owner
        UserDetailRes ownerDto = new UserDetailRes();
        ownerDto.setId(team.getOwner().getId());
        ownerDto.setFullname(team.getOwner().getFullName());
        ownerDto.setEmail(team.getOwner().getEmail());
        dto.setOwner(ownerDto);

        // Members (teamMember)
        List<TeamMember> members = teamMemberRepository.findByTeam_IdAndDeletedIsNull(team.getId());

        List<TeamMemberRes> memberResList = members.stream()
                .map(tm -> {
                    TeamMemberRes m = new TeamMemberRes();
                    m.setId(tm.getUser().getId());
                    m.setFullName(tm.getUser().getFullName());
                    m.setEmail(tm.getUser().getEmail());
                    m.setAvatarUrl(tm.getUser().getAvatarUrl());
                    m.setRole(tm.getRole().name());
                    m.setJoinedAt(tm.getCreatedAt());
                    return m;
                })
                .toList();

        dto.setMembers(memberResList);

        return dto;
    }

}
