package todo.todo.dto.request.task;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskReq {
    String title;
    String description;
    String status; // todo|in_progress|done|blocked
    String priority; // low|medium|high|urgent
    Integer assigneeId;
    Integer teamId;
    LocalDate dueDate;
}
