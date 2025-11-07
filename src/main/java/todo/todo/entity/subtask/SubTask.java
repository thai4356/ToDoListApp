package todo.todo.entity.subtask;

import jakarta.persistence.*;
import lombok.*;
import todo.todo.entity.BaseEntity;
import todo.todo.entity.task.Task;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "is_done", nullable = false)
    private boolean isDone = false;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;
}
