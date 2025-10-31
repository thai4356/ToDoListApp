package todo.todo.dto.response.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterUser {
    String email;
    String fullname;
    String avatarUrl;

    @NotNull(message = "codeId must not be null")
    int codeId;
    @NotBlank
    String code;
    @NotBlank
    String password;
    @NotBlank
    String confirmPassword;

    @AssertTrue(message = "Password don't matching")
    public boolean isValid() {
        return password.equals(confirmPassword);
    }
}
