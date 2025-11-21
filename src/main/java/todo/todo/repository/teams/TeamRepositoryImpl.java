package todo.todo.repository.teams;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import todo.todo.entity.team_member.QTeamMember;
import todo.todo.entity.team_member.TeamMember;
import todo.todo.entity.teams.QTeam;
import todo.todo.entity.teams.Team;
import todo.todo.entity.user.QUser;
import todo.todo.entity.user.User;
import todo.todo.repository.BaseRepository;

public class TeamRepositoryImpl extends BaseRepository implements TeamRepositoryCustom {

    private final QUser qUser = QUser.user;
    private final QTeam qTeam = QTeam.team;
    private final QTeamMember qTeamMember = QTeamMember.teamMember;

    @Override
    public User getHolderId(String id) {
        return query()
                .select(qUser)
                .from(qUser)
                .where(qUser.id.stringValue().eq(id))
                .fetchOne();
    }

    @Override
    public boolean existsByCode(String code) {
        Integer result = query()
                .selectOne()
                .from(qTeam)
                .where(qTeam.name.eq(code))
                .fetchFirst();

        return result != null;
    }

    @Override
    public User findByOwnerId(int id) {
        return query()
                .select(qUser)
                .from(qUser)
                .where(qUser.id.eq((int) id))
                .fetchOne();
    }

    @Override
    public Team getTeamToUpdate(int teamId) {
        Team team = query()
                .select(qTeam)
                .from(qTeam)
                .where(qTeam.id.eq((int) teamId))
                .fetchOne();

        if (team == null) {
            throw new EntityNotFoundException("Team with id " + teamId + " not found");
        }

        return team;
    }

    @Override
    public List<TeamMember> findByTeam_IdAndDeletedAtIsNull(int teamId) {
        return query()
                .select(qTeamMember)
                .from(qTeamMember)
                .where(
                        qTeamMember.team.id.eq(teamId),
                        qTeamMember.deleted.eq(false))
                .fetch();
    }

    @Override
    public List<Team> getTeams(int userId) {
        List<Team> teams = query()
                .select(qTeam)
                .from(qTeam)
                .leftJoin(qTeamMember).on(qTeamMember.team.id.eq(qTeam.id))
                .where(
                        qTeam.owner.id.eq(userId) 
                                .or(qTeamMember.user.id.eq(userId)) 
                )
                .distinct()
                .fetch();

        if (teams.isEmpty()) {
            throw new EntityNotFoundException("User " + userId + " không thuộc bất kỳ team nào");
        }

        return teams;
    }

}
