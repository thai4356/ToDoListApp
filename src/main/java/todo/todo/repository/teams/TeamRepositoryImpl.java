package todo.todo.repository.teams;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import todo.todo.entity.team_member.TeamMember;
import todo.todo.entity.teams.QTeam;
import todo.todo.entity.teams.Team;
import todo.todo.entity.user.QUser;
import todo.todo.entity.user.User;
import todo.todo.repository.BaseRepository;

public class TeamRepositoryImpl extends BaseRepository implements TeamRepositoryCustom {

    private final QUser qUser = QUser.user;
    private final QTeam qTeam = QTeam.team;

    @Override
    public User getHolderId(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHolderId'");
    }

    @Override
    public boolean existsByCode(String code) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsByCode'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByTeam_IdAndDeletedAtIsNull'");
    }

}
