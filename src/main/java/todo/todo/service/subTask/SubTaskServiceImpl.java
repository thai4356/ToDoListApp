package todo.todo.service.subTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import todo.todo.dto.response.subTask.SubtaskDetailRes;
import todo.todo.entity.subtask.SubTask;
import todo.todo.entity.task.Task;
import todo.todo.repository.subTask.SubTaskRepository;
import todo.todo.service.BaseService;

@Service
@RequiredArgsConstructor
@Transactional
public class SubTaskServiceImpl extends BaseService implements SubTaskService {

    private final SubTaskRepository subTaskRepository;

    @Override
    public List<SubtaskDetailRes> listActiveByTask(int taskId) {
        List<SubTask> list = subTaskRepository.findActiveByTaskIdOrderByIndexAsc(taskId);
        List<SubtaskDetailRes> res = new ArrayList<>();
        for (SubTask st : list) {
            res.add(toRes(st));
        }
        return res;
    }

    @Override
    public SubtaskDetailRes create(int taskId, String title) {
        if (title == null || title.trim().isEmpty())
            return null;
        String t = title.trim();

        // trùng tiêu đề (active)
        if (subTaskRepository.existsActiveTitle(taskId, t))
            return null;

        // xác định orderIndex tiếp theo
        List<SubTask> current = subTaskRepository.findActiveByTaskIdOrderByIndexAsc(taskId);
        int nextIndex = 0;
        if (current != null && !current.isEmpty()) {
            SubTask last = current.get(current.size() - 1);
            nextIndex = (last.getOrderIndex() == null ? 0 : last.getOrderIndex()) + 1;
        }

        Task taskRef = new Task();
        taskRef.setId(taskId);

        SubTask st = new SubTask();
        st.setTask(taskRef);
        st.setTitle(t);
        st.setDone(false);
        st.setOrderIndex(nextIndex);
        st.setCreatedAt(new Date());
        st.setUpdatedAt(new Date());

        st = subTaskRepository.save(st);
        return toRes(st);
    }

    @Override
    public SubtaskDetailRes rename(int subTaskId, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty())
            return null;

        // lấy subtask active
        SubTask st = subTaskRepository.findActiveById(subTaskId).orElse(null);
        if (st == null)
            return null;

        String t = newTitle.trim();
        if (!t.equalsIgnoreCase(st.getTitle())) {
            if (subTaskRepository.existsActiveTitle(st.getTask().getId(), t))
                return null;
        }

        st.setTitle(t);
        st = subTaskRepository.save(st);
        return toRes(st);
    }

    @Override
    public SubtaskDetailRes setDone(int subTaskId, boolean done) {
        SubTask st = subTaskRepository.findActiveById(subTaskId).orElse(null);
        if (st == null)
            return null;

        st.setDone(done);
        st = subTaskRepository.save(st);
        return toRes(st);
    }

    @Override
    public List<SubtaskDetailRes> reorder(int taskId, List<Integer> orderedSubTaskIds) {
        List<SubtaskDetailRes> result = new ArrayList<>();
        if (orderedSubTaskIds == null || orderedSubTaskIds.isEmpty())
            return result;

        List<SubTask> list = subTaskRepository.findActiveByTaskIdOrderByIndexAsc(taskId);
        // map đơn giản bằng vòng lặp
        for (int i = 0; i < orderedSubTaskIds.size(); i++) {
            Integer id = orderedSubTaskIds.get(i);
            for (SubTask st : list) {
                if (st.getId() == id) {
                    st.setOrderIndex(i);
                    break;
                }
            }
        }
        subTaskRepository.saveAll(list);

        for (SubTask st : list) {
            result.add(toRes(st));
        }
        return result;
    }

    @Override
    public boolean softDelete(int subTaskId) {
        int changed = subTaskRepository.softDelete(subTaskId);
        return changed > 0;
    }

    private SubtaskDetailRes toRes(SubTask st) {
        if (st == null)
            return null;
        SubtaskDetailRes r = new SubtaskDetailRes();
        r.setId(st.getId());
        r.setTaskId(st.getTask() != null ? st.getTask().getId() : 0);
        r.setTitle(st.getTitle());
        r.setIsDone(st.isDone());
        r.setOrderIndex(st.getOrderIndex());
        r.setCreatedAt(st.getCreatedAt());
        r.setUpdatedAt(st.getUpdatedAt());
        return r;
    }
}
