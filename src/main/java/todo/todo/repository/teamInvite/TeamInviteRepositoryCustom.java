package todo.todo.repository.teamInvite;

import java.util.Optional;

import todo.todo.entity.teamInvite.TeamInvite;

public interface  TeamInviteRepositoryCustom {
    TeamInvite findByToken(String token);
    Optional<TeamInvite> findByTokenAndAcceptedFalse(String token);
}
