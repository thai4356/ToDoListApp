package todo.todo.security.interceptor;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import todo.todo.component.converter.Translator;
import todo.todo.entity.user.User;
import todo.todo.exceptions.BusinessException;
import todo.todo.repository.user.UserRepository;
import todo.todo.security.JwtTokenProvider;
import todo.todo.security.SecurityContexts;

@Log4j2
@Component
public class UserInterCeptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        BusinessException exception = new BusinessException(Translator.toLocale("login_required"), HttpStatus.UNAUTHORIZED);
        String vendorCode = request.getHeader("Authorization");
        if (Strings.isEmpty(vendorCode)) {
            throw exception;
        }
        String[] header = vendorCode.split(" ");
        if (header.length != 2) {
            throw exception;
        }
        String token = header[1];
        if (jwtTokenProvider.validateTokenRs256(token)) {
            Integer sub = Integer.parseInt(jwtTokenProvider.getSubIdFromJwtRs256(token));
            SecurityContexts.newContext();
            User user = userRepository.findById(sub).orElseThrow(()-> exception);
            SecurityContexts.getContext().setData(user);
            return true;
        }
        throw exception;
    }
}
