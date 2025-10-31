package todo.todo.service.User;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import todo.todo.component.converter.Translator;
import todo.todo.dto.constant.VerifyStatus;
import todo.todo.dto.request.UserLoginReq;
import todo.todo.dto.response.BaseResponse;
import todo.todo.dto.response.user.RegisterUser;
import todo.todo.dto.response.user.UserDetailRes;
import todo.todo.dto.response.user.UserListRes;
import todo.todo.entity.otp.Otp;
import todo.todo.entity.user.User;
import todo.todo.exceptions.BusinessException;
import todo.todo.repository.otp.OtpRepository;
import todo.todo.repository.user.UserRepository;
import todo.todo.security.JwtTokenProvider;
import todo.todo.service.BaseService;
import todo.todo.util.Util;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpRepository otpRepository;
    @Value("${app.jwtAdminExpirationInMs}")
    private int jwtExpirationInMs;

    private final int OTP_EXPIRY_IN_MINUTES = 5;

    public UserServiceImpl(JwtTokenProvider jwtTokenProvider,
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetailRes login(UserLoginReq request) {
        System.out.println("Email request: " + request.getEmail());
        System.out.println("Password request: " + request.getPasswordHased());
        User user = userRepository.loginByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPasswordHased(),
                user.getPasswordHash())) {
            throw new BusinessException(Translator.toLocale("login_fail"),
                    HttpStatus.UNAUTHORIZED);
        }

        UserDetailRes userLoginRes = getUserRes(user);
        userLoginRes
                .setAccessToken(jwtTokenProvider.generateTokenRs256(String.valueOf(user.getId()), jwtExpirationInMs));
        return userLoginRes;
    }

    private UserDetailRes getUserRes(User user) {
        return UserDetailRes.builder()
                .id(user.getId())
                .fullname(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    @Override
    public BaseResponse<List<UserListRes>> getUsers() {
        List<UserListRes> users = userRepository.getUsers();
        return new BaseResponse<>(users);
    }

    @Transactional
    @Override
    public UserDetailRes register(RegisterUser request) {
        Otp sessionAuth = otpRepository.findById(request.getCodeId()).orElse(null);
        if (sessionAuth == null || sessionAuth.getStatus() == VerifyStatus.VERIFIED) {
            throw new BusinessException(Translator.toLocale("register_fail"));
        }
        try {
            if (sessionAuth.getOtp().equals(request.getCode()) && Util.getDuration(new Date(),
                    sessionAuth.getCreatedAt(), TimeUnit.MINUTES) >= OTP_EXPIRY_IN_MINUTES) {
                sessionAuth.setStatus(VerifyStatus.EXPIRED);
                throw new BusinessException(Translator.toLocale("otp_expired"));
            }
            if (sessionAuth.getAttemptCount() <= 0) {
                sessionAuth.setStatus(VerifyStatus.FAILED);
                throw new BusinessException(Translator.toLocale("otp_over"));
            }
            sessionAuth.setAttemptCount(sessionAuth.getAttemptCount() - 1);
            if (!sessionAuth.getEmail().equals(request.getEmail())) {
                sessionAuth.setStatus(VerifyStatus.FAILED);
                throw new BusinessException(Translator.toLocale("register_fail"));
            }
            if (!sessionAuth.getOtp().equals(request.getCode())) {
                throw new BusinessException(Translator.toLocale("otp_wrong"));
            }
            sessionAuth.setStatus(VerifyStatus.VERIFIED);
        } finally {
            otpRepository.save(sessionAuth);
        }

        String code = generateCode(8);
        User user = new User();
        user.setCode(code);
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullname());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        UserDetailRes userLoginRes = getUserRes(user);
        userLoginRes
                .setAccessToken(jwtTokenProvider.generateTokenRs256(String.valueOf(user.getId()), jwtExpirationInMs));
        return userLoginRes;
    }

    private String generateCode(int count) {
        String code;
        boolean exists;

        do {
            code = Util.randomString(count);
            exists = userRepository.existsByCode(code);
        } while (exists);
        return code;
    }
}
