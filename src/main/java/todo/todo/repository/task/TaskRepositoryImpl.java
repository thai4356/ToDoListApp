package todo.todo.repository.task;

import java.time.LocalDate;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import todo.todo.entity.task.QTask;
import todo.todo.entity.task.Task;

@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepositoryCustom {

    private final EntityManager em;

    private JPAQueryFactory query() {
        return new JPAQueryFactory(em);
    }

    @Override
    public List<Task> findActiveByTeam(int teamId) {
        QTask t = QTask.task;
        return query()
                .select(t)
                .from(t)
                .where(t.team.id.eq(teamId).and(t.deleted.isFalse()))
                .fetch();
    }

    @Override
    public List<Task> findByAssignee(int assigneeId) {
        QTask t = QTask.task;
        return query()
                .select(t)
                .from(t)
                .where(t.assignee.id.eq(assigneeId).and(t.deleted.isFalse()))
                .fetch();
    }

    @Override
    public List<Task> search(Integer teamId, Integer assigneeId, String status, String priority,
            LocalDate dueFrom, LocalDate dueTo) {
        QTask t = QTask.task;
        var q = query().select(t).from(t).where(t.deleted.isFalse());

        if (teamId != null)
            q = q.where(t.team.id.eq(teamId));
        if (assigneeId != null)
            q = q.where(t.assignee.id.eq(assigneeId));
        if (status != null)
            q = q.where(t.status.eq(Task.Status.valueOf(status.toLowerCase())));
        if (priority != null)
            q = q.where(t.priority.eq(Task.Priority.valueOf(priority.toLowerCase())));
        if (dueFrom != null)
            q = q.where(t.dueDate.goe(dueFrom));
        if (dueTo != null)
            q = q.where(t.dueDate.loe(dueTo));

        return q.fetch();
    }

    @Override
    public int findTeamIdByTaskId(int taskId) {
        QTask qTask = QTask.task;
        return query()
                .select(qTask.team.id)
                .from(qTask)
                .where(qTask.id.eq(taskId))
                .fetchOne();
    }
}
