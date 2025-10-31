package todo.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import todo.todo.dto.request.UserLoginReq;
import todo.todo.dto.request.otp.SendOtpReq;
import todo.todo.dto.response.BaseResponse;
import todo.todo.dto.response.otp.SendOtp;
import todo.todo.dto.response.user.RegisterUser;
import todo.todo.dto.response.user.UserDetailRes;
import todo.todo.service.User.UserService;
import todo.todo.service.otp.OtpService;

@RestController
@RequestMapping("/api/")
public class AuthController {
    private final UserService userService;
    private final OtpService otpService;

    public AuthController(OtpService otpService, UserService userService) {
        this.otpService = otpService;
        this.userService = userService;
    }
    
    @Operation(summary = "Login")
    @PostMapping("v1/auth/login")
    public ResponseEntity<BaseResponse<UserDetailRes>> loginUser(@RequestBody @Valid UserLoginReq request) {
        return ResponseEntity.ok(new BaseResponse<>(userService.login(request)));
    }

    @Operation(summary = "Register")
    @PostMapping("v1/auth/register")
    public ResponseEntity<BaseResponse<UserDetailRes>> registerUser(@RequestBody @Valid RegisterUser request) {
        return ResponseEntity.ok(new BaseResponse<>(userService.register(request)));
    }

    @Operation(summary = "Send OTP forgot password")
    @PostMapping("v1/auth/send-otp")
    public ResponseEntity<BaseResponse<SendOtp>> sendOtpUser(@RequestBody @Valid SendOtpReq request) {
        return ResponseEntity.ok(new BaseResponse<>(otpService.sendOtpUser(request)));
    }
}
