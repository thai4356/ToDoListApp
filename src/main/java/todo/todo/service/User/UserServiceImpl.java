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
import todo.todo.entity.upload_file.UploadFile;
import todo.todo.entity.user.User;
import todo.todo.exceptions.BusinessException;
import todo.todo.repository.media.MediaRepository;
import todo.todo.repository.otp.OtpRepository;
import todo.todo.repository.user.UserRepository;
import todo.todo.security.JwtTokenProvider;
import todo.todo.security.SecurityContexts;
import todo.todo.service.BaseService;
import todo.todo.service.file.FileStorageService;
import todo.todo.util.Util;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpRepository otpRepository;
    private final MediaRepository mediaRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.jwtAdminExpirationInMs}")
    private int jwtExpirationInMs;

    private final int OTP_EXPIRY_IN_MINUTES = 5;

    public UserServiceImpl(FileStorageService fileStorageService, JwtTokenProvider jwtTokenProvider,
            MediaRepository mediaRepository, OtpRepository otpRepository, PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.fileStorageService = fileStorageService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mediaRepository = mediaRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    private int uid() {
        return getUser().getId();
    }

    @Override
    public UserDetailRes login(UserLoginReq request) {
        System.out.println("Email request: " + request.getEmail());
        System.out.println("Password request: " + request.getPasswordHashed());
        User user = userRepository.loginByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPasswordHashed(),
                user.getPasswordHash())) {
            throw new BusinessException(Translator.toLocale("login_fail"),
                    HttpStatus.UNAUTHORIZED);
        }

        SecurityContexts.newContext();
        SecurityContexts.getContext().setData(user);
        System.out.println("User in SecurityContexts: " + SecurityContexts.getContext().getData());

        UserDetailRes userLoginRes = getUserRes(user);
        userLoginRes
                .setAccessToken(jwtTokenProvider.generateTokenRs256(String.valueOf(user.getId()), jwtExpirationInMs));
        return userLoginRes;
    }

    public static UserDetailRes from(User user) {
        UserDetailRes res = new UserDetailRes();
        res.setId(user.getId());
        res.setFullname(user.getFullName());
        res.setEmail(user.getEmail());

        if (user.getAvatarFile() != null) {
            res.setAvaId(user.getAvatarFile().getId());
            res.setAvatarUrl(user.getAvatarFile().getThumbUrl() != null
                    ? user.getAvatarFile().getThumbUrl()
                    : user.getAvatarFile().getOriginUrl());
        }

        return res;
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

        System.out.println(request.getFullname());

        String code = generateCode(8);
        User user = new User();
        user.setCode(code);
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullname());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {

            UploadFile avatar = fileStorageService.storeImage(request.getAvatar());
            avatar.setUser(user);
            avatar.setCreatedAt(new Date());
            avatar.setUpdatedAt(new Date());
            mediaRepository.save(avatar);

            user.setAvatarFile(avatar);
            userRepository.save(user);
        }
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

    @Override
    public User getUserByEmail() {
        User user = getUser();
        return user;
    }

    public UserDetailRes getUserRes(User user) {
        UserDetailRes res = new UserDetailRes();
        res.setId(user.getId());
        res.setFullname(user.getFullName());
        res.setEmail(user.getEmail());
        res.setAccessToken(user.getCode());
        res.setAvatarUrl(user.getAvatarUrl());
        res.setAvaId(
                user.getAvatarFile() != null ? user.getAvatarFile().getId() : null);
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }
}
