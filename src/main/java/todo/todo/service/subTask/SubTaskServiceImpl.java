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
import todo.todo.repository.task.TaskRepository;
import todo.todo.repository.teamMember.TeamMemberRepository;
import todo.todo.service.BaseService;
import todo.todo.service.PermissionGuard;

@Service
@RequiredArgsConstructor
@Transactional
public class SubTaskServiceImpl extends BaseService implements SubTaskService {

    private final PermissionGuard guard;
    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepo;
    private final TeamMemberRepository teamMemberRepository;

    private int uid() {
        return getUser().getId();
    }

    @Override
    public List<SubtaskDetailRes> listActiveByTask(int taskId) {

        guard.requireOwnerAdminForTask(uid(), taskId);
        List<SubTask> list = subTaskRepository.findActiveByTaskIdOrderByIndexAsc(taskId);
        List<SubtaskDetailRes> res = new ArrayList<>();
        for (SubTask st : list)
            res.add(toRes(st));
        return res;
    }

    @Override
    public SubtaskDetailRes create(int taskId, String title) {
        int uid = uid();

        Integer teamId = null;
        try {
            Task tRef = taskRepo.getReferenceById(taskId);
            teamId = (tRef.getTeam() != null ? tRef.getTeam().getId() : null);
            System.out.println("[DEBUG] teamId of task=" + teamId);
        } catch (Exception ex) {
            System.out.println("[DEBUG] cannot resolve teamId for taskId=" + taskId + " -> " + ex.getMessage());
        }
        try {

            boolean hasPower = teamMemberRepository.existsOwnerAdminInTeam(uid,
                    teamId == null ? -1 : teamId,
                    new java.util.HashSet<>(java.util.Arrays.asList(
                            todo.todo.entity.team_member.TeamMember.Role.owner,
                            todo.todo.entity.team_member.TeamMember.Role.admin)));
            System.out.println("[DEBUG] existsOwnerAdminInTeam -> " + hasPower);
        } catch (Exception ex) {
            System.out.println("[DEBUG] existsOwnerAdminInTeam check error -> " + ex.getMessage());
        }

        try {
            guard.requireOwnerAdminForSubTask(uid, taskId);
            System.out.println("[GUARD] Passed permission check ");
        } catch (Exception e) {
            System.out.println("[GUARD] Permission check failed " + e.getMessage());
            throw e;
        }

        if (title == null || title.trim().isEmpty()) {
            System.out.println("[INFO] Title empty → return null");
            return null;
        }
        String t = title.trim();

        if (subTaskRepository.existsActiveTitle(taskId, t)) {
            System.out.println("[INFO] Duplicated title in task → return null");
            return null;
        }

        java.util.List<SubTask> current = subTaskRepository.findActiveByTaskIdOrderByIndexAsc(taskId);
        int nextIndex = 0;
        if (current != null && !current.isEmpty()) {
            SubTask last = current.get(current.size() - 1);
            Integer li = last.getOrderIndex();
            nextIndex = (li == null ? 0 : li) + 1;
        }
        System.out.println("[DEBUG] nextIndex=" + nextIndex);

        Task taskRef = taskRepo.getReferenceById(taskId);

        SubTask st = new SubTask();
        st.setTask(taskRef);
        st.setTitle(t);
        st.setDone(false);
        st.setOrderIndex(nextIndex);
        st.setCreatedAt(new java.util.Date());
        st.setUpdatedAt(new java.util.Date());

        st = subTaskRepository.save(st);
        System.out.println("[SUCCESS] SubTask created id=" + st.getId() + " | orderIndex=" + nextIndex);
        return toRes(st);
    }

    @Override
    public SubtaskDetailRes rename(int subTaskId, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty())
            return null;

        SubTask st = subTaskRepository.findActiveById(subTaskId).orElse(null);
        if (st == null)
            return null;

        guard.requireOwnerAdminForSubTask(uid(), subTaskId);

        String t = newTitle.trim();
        if (!t.equalsIgnoreCase(st.getTitle())) {
            if (subTaskRepository.existsActiveTitle(st.getTask().getId(), t))
                return null;
        }

        st.setTitle(t);
        st.setUpdatedAt(new Date());
        st = subTaskRepository.save(st);
        return toRes(st);
    }

    @Override
    public SubtaskDetailRes setDone(int subTaskId, boolean done) {
        SubTask st = subTaskRepository.findActiveById(subTaskId).orElse(null);
        if (st == null)
            return null;

        guard.requireOwnerAdminForSubTask(uid(), subTaskId);

        st.setDone(done);
        st.setUpdatedAt(new Date());
        st = subTaskRepository.save(st);
        return toRes(st);
    }

    @Override
    public List<SubtaskDetailRes> reorder(int taskId, List<Integer> orderedSubTaskIds) {
        guard.requireOwnerAdminForTask(uid(), taskId);

        List<SubtaskDetailRes> result = new ArrayList<>();
        if (orderedSubTaskIds == null || orderedSubTaskIds.isEmpty())
            return result;

        List<SubTask> list = subTaskRepository.findActiveByTaskIdOrderByIndexAsc(taskId);

        for (int i = 0; i < orderedSubTaskIds.size(); i++) {
            Integer id = orderedSubTaskIds.get(i);
            for (SubTask st : list) {
                if (st.getId() == id) {
                    st.setOrderIndex(i);
                    st.setUpdatedAt(new Date());
                    break;
                }
            }
        }
        subTaskRepository.saveAll(list);

        for (SubTask st : list)
            result.add(toRes(st));
        return result;
    }

    @Override
    public boolean softDelete(int subTaskId) {

        guard.requireOwnerAdminForSubTask(uid(), subTaskId);

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
