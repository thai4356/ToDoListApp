package todo.todo.repository.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import todo.todo.entity.task.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>, TaskRepositoryCustom {
    // List<TeamMember> findByTeam_IdAndDeletedIsNull(int teamId);

    // TeamMember findByUserId(int userId);

    // TeamMember findByUserIdAndTeamId(int userId, int teamId);

}
