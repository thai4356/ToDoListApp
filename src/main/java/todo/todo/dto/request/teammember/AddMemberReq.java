package todo.todo.dto.request.teammember;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberReq {

    @NotNull(message = "email is required")
    private String email;

    private String role;
}
