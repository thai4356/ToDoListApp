package todo.todo.entity.otp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import todo.todo.dto.constant.SendType;
import todo.todo.dto.constant.VerifyStatus;
import todo.todo.entity.BaseEntity;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "otp")
@Entity
public class Otp extends BaseEntity {
    String otp;
    String email;

    @Column(name = "attempt_count")
    int attemptCount = 0;

    @Column(name = "send_type", columnDefinition = "INT")
    SendType sendType;

    @Column(name = "purpose", columnDefinition = "INT")
    int purpose;

    @Column(name = "status", columnDefinition = "INT")
    VerifyStatus status;
}
