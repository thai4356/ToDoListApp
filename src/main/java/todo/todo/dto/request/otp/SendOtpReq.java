package todo.todo.dto.request.otp;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import todo.todo.dto.constant.OtpSendPurpose;
import todo.todo.dto.constant.SendType;

@Data
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendOtpReq {
    String email;
    @NotNull
    SendType type;
    @NotNull
    OtpSendPurpose purpose;

    @AssertTrue(message = "email must be provided")
    public boolean isValid() {
        return email != null;
    }
}