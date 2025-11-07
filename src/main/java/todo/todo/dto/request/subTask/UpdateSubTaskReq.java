package todo.todo.dto.request.subTask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSubTaskReq {
    String title;
    Boolean isDone;     
    Integer orderIndex; 
}