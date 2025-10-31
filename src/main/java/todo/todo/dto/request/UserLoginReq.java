package todo.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginReq {
    @NotBlank
    String email;
    @NotBlank
    String passwordHased;

}
