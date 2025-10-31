package todo.todo.repository.teamMember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import todo.todo.entity.team_member.TeamMember;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer>, TeamMemberRepositoryCustom {
        
}
