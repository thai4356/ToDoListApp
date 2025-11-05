package todo.todo.dto.request.task;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
 import lombok.Setter;

@Getter @Setter
public class AddTaskBaseReq {
    Integer teamId;          // optional
    Integer assigneeId;      // optional
    @NotBlank String title;
    String description;
    String status;           // todo|in_progress|done|blocked (optional)
    String priority;         // low|medium|high|urgent (optional)
    LocalDate dueDate;       // optional
}
