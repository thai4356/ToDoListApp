package todo.todo.repository.teamMember;

import java.util.List;

import org.springframework.stereotype.Repository;

import todo.todo.entity.team_member.TeamMember;

@Repository
public interface TeamMemberRepositoryCustom {
    List<TeamMember> findByTeam_IdAndDeletedIsNull(int teamId);
    TeamMember findByUserId(int userId);
    TeamMember findByUserIdAndTeamId(int userId, int teamId);
    List<TeamMember> findActiveByTeam(int teamId);
}
