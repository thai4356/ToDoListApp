package todo.todo.repository.teamInvite;

import java.util.Optional;

import todo.todo.entity.teamInvite.QTeamInvite;
import todo.todo.entity.teamInvite.TeamInvite;
import todo.todo.repository.BaseRepository;

public class TeamInviteRepositoryImpl extends BaseRepository implements TeamInviteRepositoryCustom {

    private final QTeamInvite qTeamInvite = QTeamInvite.teamInvite;

    @Override
    public TeamInvite findByToken(String token) {
        return query()
                .select(qTeamInvite)
                .from(qTeamInvite)
                .where(
                        qTeamInvite.token.eq(token),
                        qTeamInvite.deleted.isFalse())
                .fetchOne();
    }

    @Override
    public Optional<TeamInvite> findByTokenAndAcceptedFalse(String token) {
        TeamInvite invite = query()
                .select(qTeamInvite)
                .from(qTeamInvite)
                .where(
                        qTeamInvite.token.eq(token),
                        qTeamInvite.accepted.isFalse())
                .fetchOne();

        return Optional.ofNullable(invite);
    }
}
