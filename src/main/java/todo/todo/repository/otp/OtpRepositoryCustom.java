package todo.todo.repository.otp;

import todo.todo.dto.request.otp.SendOtpReq;

public interface OtpRepositoryCustom {
       void updateStatusVerify (SendOtpReq request);
}
