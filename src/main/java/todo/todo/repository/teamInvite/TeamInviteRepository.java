package todo.todo.repository.teamInvite;

import org.springframework.data.jpa.repository.JpaRepository;

import todo.todo.entity.teamInvite.TeamInvite;
import todo.todo.entity.team_member.TeamMember;

public interface TeamInviteRepository  extends JpaRepository<TeamInvite, Integer>, TeamInviteRepositoryCustom{
    
}
