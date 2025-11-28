package todo.todo.repository.teamMember;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import todo.todo.dto.response.teamMember.TeamMemberRes;
import todo.todo.entity.team_member.TeamMember;
import todo.todo.entity.team_member.TeamMember.Role;

@Repository
public interface TeamMemberRepositoryCustom {
    List<TeamMember> findByTeam_IdAndDeletedIsNull(int teamId);

    TeamMember findByUserId(int userId);

    TeamMember findByUserIdAndTeamId(int userId, int teamId);

    List<TeamMember> findActiveByTeam(int teamId);

    boolean existsOwnerAdminInTeam(int userId, int teamId, Set<Role> roles);

    List<TeamMemberRes> findActiveMemberDtosByTeamId(int teamId);

}
