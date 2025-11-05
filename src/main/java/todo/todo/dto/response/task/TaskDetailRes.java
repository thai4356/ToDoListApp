package todo.todo.dto.response.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
 import lombok.Value;
import todo.todo.dto.response.user.UserMiniRes;

@Value @Builder
public class TaskDetailRes {
    int id;
    Integer teamId;
    String title;
    String description;
    String status;     // enum name
    String priority;   // enum name
    LocalDate dueDate;
    UserMiniRes creator;
    UserMiniRes assignee;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Boolean deleted;
    LocalDateTime deletedAt;
}