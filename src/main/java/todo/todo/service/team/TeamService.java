package todo.todo.service.team;

import java.util.List;

import todo.todo.dto.request.team.AddTeamBaseReq;
import todo.todo.dto.response.team.TeamDetailRes;
import todo.todo.dto.response.teamMember.TeamMemberRes;

public interface TeamService {
    void AssignJob();
    TeamDetailRes CreateTeam(AddTeamBaseReq request, int currentUserId);
    TeamDetailRes UpdateTeam(AddTeamBaseReq request , int foundId , int currentUserId);
    TeamDetailRes DeleteTeam(int teamId, int currentUserId);
    TeamMemberRes inviteMember(int teamId, String email, int currentUserId);
    TeamMemberRes updateMemberRole(int teamId, int userId, String newRole);
    TeamDetailRes getTeamDetail(int teamId);
    TeamMemberRes deleteMemberFromTeam(int teamId, int userId, int currentUserId);
    List<TeamDetailRes> getTeamsByUser(int userId);
    String acceptInvite(String token);
}
