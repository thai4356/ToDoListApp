package todo.todo.repository.teams;

import java.util.List;

import todo.todo.entity.team_member.TeamMember;
import todo.todo.entity.teams.Team;
import todo.todo.entity.user.User;

public interface TeamRepositoryCustom {

    User getHolderId(String id);

    boolean existsByCode (String code);

    User findByOwnerId(int id);

    Team getTeamToUpdate(int teamId);

    List<TeamMember> findByTeam_IdAndDeletedAtIsNull(int teamId);

    List<Team> getTeams (int userId);
}
