package todo.todo.entity.teamInvite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import todo.todo.entity.BaseEntity;

@Entity
@Table(name = "team_invites")
@Getter
@Setter
public class TeamInvite extends BaseEntity {

    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "invited_user_id")
    private Integer invitedUserId;

    @Column(name = "invited_by_user_id")
    private Integer invitedByUserId;

    @Column(name = "token")
    private String token;

    @Column(name = "accepted")
    private Boolean accepted = false;
}
