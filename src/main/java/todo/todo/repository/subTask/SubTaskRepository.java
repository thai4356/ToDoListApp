package todo.todo.repository.subTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import todo.todo.entity.subtask.SubTask;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Integer>, SubTaskRepositoryCustom {
    
}
