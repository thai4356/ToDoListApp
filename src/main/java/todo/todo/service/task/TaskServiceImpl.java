package todo.todo.service.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import todo.todo.dto.request.task.AddTaskBaseReq;
import todo.todo.dto.request.task.UpdateTaskReq;
import todo.todo.dto.response.task.TaskDetailRes;
import todo.todo.dto.response.user.UserMiniRes;
import todo.todo.entity.task.Task;
import todo.todo.entity.teams.Team;
import todo.todo.entity.user.User;
import todo.todo.repository.task.TaskRepository;
import todo.todo.repository.teams.TeamRepository;
import todo.todo.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    public TaskDetailRes createTask(AddTaskBaseReq req, int currentUserId) {
        User creator = mustGetUser(currentUserId);
        Team team = req.getTeamId() != null ? mustGetTeam(req.getTeamId()) : null;
        User assignee = req.getAssigneeId() != null ? mustGetUser(req.getAssigneeId()) : null;

        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setTeam(team);
        t.setCreator(creator);
        t.setAssignee(assignee);
        t.setStatus(parseStatusOrDefault(req.getStatus(), Task.Status.todo));
        t.setPriority(parsePriorityOrDefault(req.getPriority(), Task.Priority.medium));
        t.setDueDate(req.getDueDate());
        t.setDeleted(false);
        taskRepository.save(t);

        return toRes(t);
    }

    @Override
    public TaskDetailRes updateTask(int taskId, UpdateTaskReq req, int currentUserId) {
        Task t = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        if (req.getTitle() != null && !req.getTitle().isBlank())
            t.setTitle(req.getTitle());
        if (req.getDescription() != null)
            t.setDescription(req.getDescription());
        if (req.getTeamId() != null)
            t.setTeam(mustGetTeam(req.getTeamId()));
        if (req.getAssigneeId() != null)
            t.setAssignee(mustGetUser(req.getAssigneeId()));
        if (req.getStatus() != null)
            t.setStatus(parseStatusOrDefault(req.getStatus(), t.getStatus()));
        if (req.getPriority() != null)
            t.setPriority(parsePriorityOrDefault(req.getPriority(), t.getPriority()));
        if (req.getDueDate() != null)
            t.setDueDate(req.getDueDate());

        taskRepository.save(t);
        return toRes(t);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDetailRes getTask(int taskId) {
        Task t = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        return toRes(t);
    }

    @Override
    public String deleteTask(int taskId, int currentUserId) {
        Task t = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        t.setDeleted(true);
        t.setDeleted(true);
        taskRepository.save(t);

        return "Xóa task thành công task: " + t.getTitle();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDetailRes> listByTeam(int teamId, boolean includeDeleted) {
        List<Task> list = includeDeleted
                ? taskRepository.findAll().stream().filter(x -> x.getTeam() != null && x.getTeam().getId() == teamId)
                        .toList()
                : taskRepository.findActiveByTeam(teamId);
        return list.stream().map(this::toRes).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDetailRes> search(Integer teamId, Integer assigneeId, String status,
            String priority, LocalDate dueFrom, LocalDate dueTo) {
        return taskRepository.search(teamId, assigneeId, status, priority, dueFrom, dueTo)
                .stream().map(this::toRes).toList();
    }

    private User mustGetUser(int id) {
        User u = userRepository.findById(id);
        if (u == null)
            throw new RuntimeException("User not found: " + id);
        return u;
    }

    private Team mustGetTeam(int id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found: " + id));
    }

    private Task.Status parseStatusOrDefault(String s, Task.Status def) {
        if (s == null || s.isBlank())
            return def;
        try {
            return Task.Status.valueOf(s.trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    private Task.Priority parsePriorityOrDefault(String s, Task.Priority def) {
        if (s == null || s.isBlank())
            return def;
        try {
            return Task.Priority.valueOf(s.trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    private TaskDetailRes toRes(Task t) {
        return TaskDetailRes.builder()
                .id(t.getId())
                .teamId(t.getTeam() != null ? t.getTeam().getId() : null)
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus().name())
                .priority(t.getPriority().name())
                .dueDate(t.getDueDate())
                .creator(toMini(t.getCreator()))
                .assignee(t.getAssignee() != null ? toMini(t.getAssignee()) : null)
                .createdAt(toLdt(t.getCreatedAt()))
                .updatedAt(toLdt(t.getUpdatedAt()))
                .deleted(t.isDeleted())
                .build();
    }

    private UserMiniRes toMini(User u) {
        return UserMiniRes.builder()
                .id(u.getId())
                .fullname(u.getFullName())
                .email(u.getEmail())
                .avatarUrl(u.getAvatarUrl())
                .build();
    }

    private LocalDateTime toLdt(Date d) {
        return d == null ? null : LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }
}
