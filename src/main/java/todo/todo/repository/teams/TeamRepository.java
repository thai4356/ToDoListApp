package todo.todo.repository.teams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import todo.todo.entity.teams.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer>, TeamRepositoryCustom {
        
}