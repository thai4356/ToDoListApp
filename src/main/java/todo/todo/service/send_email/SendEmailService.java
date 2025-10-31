package todo.todo.service.send_email;

import todo.todo.entity.email.EmailDetail;


public interface SendEmailService {
    void sendSimpleMail(EmailDetail details);
}

