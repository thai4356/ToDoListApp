package todo.todo.security.interceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import todo.todo.component.converter.Translator;
import todo.todo.entity.user.User;
import todo.todo.exceptions.BusinessException;
import todo.todo.repository.user.UserRepository;
import todo.todo.security.JwtTokenProvider;
import todo.todo.security.SecurityContexts;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserInterCeptor implements HandlerInterceptor {

    private static final String AUTH = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler) throws Exception {

        BusinessException unauthorized = new BusinessException(Translator.toLocale("login_required"),
                HttpStatus.UNAUTHORIZED);

        String auth = request.getHeader(AUTH);
        if (auth == null || auth.isBlank() || !auth.startsWith(BEARER)) {
            log.debug("[AUTH] Missing/invalid Authorization header");
            throw unauthorized;
        }

        String token = auth.substring(BEARER.length()).trim();
        boolean valid = false;
        try {
            valid = jwtTokenProvider.validateTokenRs256(token);
        } catch (Exception ex) {
            log.debug("[AUTH] JWT validate exception: {}", ex.getMessage());
        }
        if (!valid) {
            log.debug("[AUTH] JWT validation failed");
            throw unauthorized;
        }

        String subStr;
        int userId;
        try {
            subStr = jwtTokenProvider.getSubIdFromJwtRs256(token);
            userId = Integer.parseInt(subStr);
        } catch (Exception ex) {
            log.debug("[AUTH] Invalid sub claim: {}", ex.getMessage());
            throw unauthorized;
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            log.debug("[AUTH] User not found: {}", userId);
            throw unauthorized;
        }

        SecurityContexts.newContext();
        SecurityContexts.getContext().setData(user);

        log.debug("[AUTH] OK userId={}, path={}", user.getId(), request.getRequestURI());
        return true;
    }
}
