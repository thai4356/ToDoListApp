package todo.todo.dto.response.team;

import java.util.Date;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import todo.todo.dto.response.teamMember.TeamMemberRes;
import todo.todo.dto.response.user.UserDetailRes;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamDetailRes {
    int id;
    String name;
    String description;
    UserDetailRes owner;        
    List<TeamMemberRes> members;
    Date createdAt;
    Date updatedAt;
}
