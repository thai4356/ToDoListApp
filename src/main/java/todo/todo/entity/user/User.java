package todo.todo.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import todo.todo.entity.BaseEntity;
import todo.todo.entity.upload_file.UploadFile;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "users")
public class User extends BaseEntity {

    String fullName;
    String email;
    String code;
    String passwordHash;
    String avatarUrl;

    @OneToOne
    @JoinColumn(name = "avatar_file_id")
    private UploadFile avatarFile;

}
