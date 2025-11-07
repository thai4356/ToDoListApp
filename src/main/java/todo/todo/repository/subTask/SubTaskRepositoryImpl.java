package todo.todo.repository.subTask;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import todo.todo.entity.subtask.QSubTask;
import todo.todo.entity.subtask.SubTask;
import todo.todo.repository.BaseRepository;

@Repository
public class SubTaskRepositoryImpl extends BaseRepository implements SubTaskRepositoryCustom {

        private final QSubTask qSubTask = QSubTask.subTask;

        @Override
        public List<SubTask> findActiveByTaskIdOrderByIndexAsc(int taskId) {
                return query()
                                .select(qSubTask)
                                .from(qSubTask)
                                .where(
                                                qSubTask.task.id.eq(taskId),
                                                qSubTask.deleted.eq(false))
                                .orderBy(
                                                qSubTask.orderIndex.asc(),
                                                qSubTask.id.asc())
                                .fetch();
        }

        @Override
        public Optional<SubTask> findActiveById(int id) {
                SubTask s = query()
                                .select(qSubTask)
                                .from(qSubTask)
                                .where(
                                                qSubTask.id.eq(id),
                                                qSubTask.deleted.eq(false))
                                .fetchOne();
                return Optional.ofNullable(s);
        }

        @Override
        public boolean existsActiveTitle(int taskId, String title) {
                QSubTask q = QSubTask.subTask;
                return query()
                                .selectOne()
                                .from(q)
                                .where(
                                                q.task.id.eq(taskId),
                                                q.title.eq(title),
                                                q.deleted.isFalse())
                                .limit(1)
                                .fetchFirst() != null; 
        }

        @Override
        public int softDelete(int id) {
                long changed = query()
                                .update(qSubTask)
                                .where(
                                                qSubTask.id.eq(id),
                                                qSubTask.deleted.eq(false))
                                .set(qSubTask.deleted, true)
                                .execute();
                return (int) changed;
        }
}
