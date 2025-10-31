package todo.todo.dto.response.teamMember;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamMemberRes {
    int id;                // user_id
    String fullName;
    String email;
    String avatarUrl;
    String role;            // 'owner', 'admin', 'member'

    LocalDateTime joinedAt;
}
