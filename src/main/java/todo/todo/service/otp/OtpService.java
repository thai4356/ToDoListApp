package todo.todo.service.otp;

import todo.todo.dto.request.otp.SendOtpReq;
import todo.todo.dto.response.otp.SendOtp;

public interface OtpService {
    SendOtp sendOtpUser (SendOtpReq request);
}
