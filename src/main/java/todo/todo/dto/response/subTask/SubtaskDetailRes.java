package todo.todo.dto.response.subTask;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubtaskDetailRes {
    int id;
    int taskId;
    String title;
    Boolean isDone;
    Integer orderIndex;
    Date createdAt;
    Date updatedAt;
}