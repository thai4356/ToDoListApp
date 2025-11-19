package todo.todo.service.otp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import todo.todo.component.converter.Translator;
import todo.todo.dto.constant.OtpSendPurpose;
import todo.todo.dto.constant.VerifyStatus;
import todo.todo.dto.request.otp.SendOtpReq;
import todo.todo.dto.response.otp.SendOtp;
import todo.todo.entity.email.EmailDetail;
import todo.todo.entity.otp.Otp;
import todo.todo.entity.user.User;
import todo.todo.exceptions.BusinessException;
import todo.todo.repository.otp.OtpRepository;
import todo.todo.repository.user.UserRepository;
import todo.todo.service.BaseService;
import todo.todo.service.send_email.SendEmailServiceImpl;
import todo.todo.util.Constants;
import todo.todo.util.Util;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl extends BaseService implements OtpService {

    @Autowired
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;
    private final SendEmailServiceImpl sendEmailService;

    @Override
    public SendOtp sendOtpUser(SendOtpReq request) {
        User user = userRepository.getUserByEmail(request.getEmail());
        if (request.getType() == null || request.getPurpose() == null) {
            throw new BusinessException(Translator.toLocale("invalid_request"), HttpStatus.BAD_REQUEST);
        }
        if (user != null && request.getPurpose().equals(OtpSendPurpose.REGISTRATION)) {
            throw new BusinessException(Translator.toLocale("phone_already_exists"), HttpStatus.BAD_REQUEST);
        }
        if (user == null && request.getPurpose().equals(OtpSendPurpose.PASSWORD_RESET)) {
            throw new BusinessException(Translator.toLocale("phone_no_exists"), HttpStatus.BAD_REQUEST);
        }
        return sendOtp(request, user);
    }

    private SendOtp sendOtp(SendOtpReq request, Object object) {

        otpRepository.updateStatusVerify(request);

        Otp otp = new Otp();
        otp.setEmail(request.getEmail());
        otp.setSendType(request.getType());
        otp.setOtp(Util.randomString(6, Constants.DIGITS));
        otp.setStatus(VerifyStatus.VERIFY_PENDING);
        otp.setCreatedAt(new Date());
        otp.setPurpose(request.getPurpose().toValue());
        otp.setAttemptCount(otpRepository.findByEmail(request.getEmail())+1);
        
        otpRepository.save(otp);

        System.out.println(request.getEmail());
        System.out.println(request.getType());
        System.out.println(request.getPurpose());

        SendOtp result = new SendOtp();
        switch (request.getType()) {
            case EMAIL:
                sendEmail(request, otp.getOtp(), request.getPurpose());
                result.setId(otp.getId());
                result.setEmail(otp.getEmail());
                break;
            default:
                throw new BusinessException(Translator.toLocale("invalid_request"), HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    private void sendEmail(SendOtpReq request, String otp, OtpSendPurpose purpose) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(request.getEmail());

        // Subject đơn giản (tuỳ mục đích)
        String subject = (purpose == OtpSendPurpose.REGISTRATION)
                ? "Your registration OTP"
                : "Your password reset OTP";
        emailDetail.setSubject(subject);

        // Nội dung chỉ chứa mã OTP (text thuần)
        // Có thể bổ sung thời hạn tuỳ ý (vd: 5 phút)
        String body = "OTP: " + otp + "\nThis code will expire in 5 minutes.";
        emailDetail.setMsgBody(body);

        // Gửi như bình thường
        sendEmailService.sendSimpleMail(emailDetail);
    }

    private String readTemplate(String templateName) {
        Resource resource = resourceLoader.getResource("classpath:templates/" + templateName);
        try (InputStream inputStream = resource.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
