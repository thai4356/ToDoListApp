package todo.todo.service.team;

import todo.todo.dto.request.team.AddTeamBaseReq;
import todo.todo.dto.response.team.TeamDetailRes;
import todo.todo.dto.response.teamMember.TeamMemberRes;

public interface TeamService {
    void AssignJob();
    TeamDetailRes CreateTeam(AddTeamBaseReq request, int currentUserId);
    TeamDetailRes UpdateTeam(AddTeamBaseReq request , int foundId , int currentUserId);
    TeamDetailRes DeleteTeam(int teamId, int currentUserId);
    TeamMemberRes addMemberToTeam(int teamId, int userId, int currentUserId);
    TeamMemberRes   updateMemberRole(int teamId, int userId, String newRole);
    TeamDetailRes getTeamDetail(int teamId);
    TeamMemberRes deleteMemberFromTeam(int teamId, int userId, int currentUserId);
}
