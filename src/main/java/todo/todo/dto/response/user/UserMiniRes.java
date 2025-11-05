package todo.todo.dto.response.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserMiniRes {
    int id;
    String fullname;
    String email;
    String avatarUrl;
}