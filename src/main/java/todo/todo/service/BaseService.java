package todo.todo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import todo.todo.component.converter.Translator;
import todo.todo.entity.user.User;
import todo.todo.exceptions.BusinessException;
import todo.todo.security.SecurityContexts;


@Service
public class BaseService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected User getUser() {
        try {
            return (User) SecurityContexts.getContext().getData();
        } catch (Exception e) {
            log.error("Error retrieving user from security context", e);
        }
        throw new BusinessException(Translator.toLocale("login_required"),HttpStatus.UNAUTHORIZED);
    }
}
