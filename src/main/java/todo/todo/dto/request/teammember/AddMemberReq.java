package todo.todo.dto.request.teammember;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberReq {

    @NotNull(message = "userId is required")
    @Min(value = 1, message = "userId must be > 0")
    private int userId;

    private String role;
}
