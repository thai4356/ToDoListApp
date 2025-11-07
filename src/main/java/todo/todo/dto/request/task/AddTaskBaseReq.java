package todo.todo.dto.request.task;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTaskBaseReq {
    Integer teamId; 
    Integer assigneeId; 
    @NotBlank
    String title;
    String description;
    String status; 
    String priority; 
    LocalDate dueDate; 
}
