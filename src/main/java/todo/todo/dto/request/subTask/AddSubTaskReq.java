package todo.todo.dto.request.subTask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddSubTaskReq {
    int taskId;
    String title;
    Integer orderIndex;
}
