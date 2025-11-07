package todo.todo.repository.task;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import todo.todo.entity.task.Task;

@Repository
public interface TaskRepositoryCustom {
   
    List<Task> findActiveByTeam(int teamId);

    List<Task> findByAssignee(int assigneeId);

    List<Task> search(Integer teamId, Integer assigneeId, String status, String priority, LocalDate dueFrom, LocalDate dueTo);

    int findTeamIdByTaskId(int taskId) ;
}
