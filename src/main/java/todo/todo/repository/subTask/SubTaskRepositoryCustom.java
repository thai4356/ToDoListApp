package todo.todo.repository.subTask;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import todo.todo.entity.subtask.SubTask;

@Repository
public interface SubTaskRepositoryCustom {
    List<SubTask> findActiveByTaskIdOrderByIndexAsc(int taskId);
    Optional<SubTask> findActiveById(int id);
    boolean existsActiveTitle(int taskId, String title);
    int softDelete(int id);
}