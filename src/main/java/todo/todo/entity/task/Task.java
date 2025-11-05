package todo.todo.entity.task;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;

import todo.todo.entity.BaseEntity;
import todo.todo.entity.teams.Team;
import todo.todo.entity.user.User;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Task extends BaseEntity {

    // team_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_tasks_team"))
    private Team team;

    // creator_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tasks_creator"))
    private User creator;

    // assignee_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", foreignKey = @ForeignKey(name = "fk_tasks_assignee"))
    private User assignee;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('todo','in_progress','done','blocked')")
    private Status status = Status.todo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('low','medium','high','urgent')")
    private Priority priority = Priority.medium;

    private LocalDate dueDate;

    public enum Status {
        todo, in_progress, done, blocked
    }

    public enum Priority {
        low, medium, high, urgent
    }
}
