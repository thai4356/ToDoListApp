package todo.todo.dto.response.user;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailRes {
    int id;
    String email;
    String fullname;
    String avatarUrl;
    String accessToken;
    int avaId;
    private Date createdAt;
    private Date updatedAt;

    public int getId() {
        return id;
    }
}
