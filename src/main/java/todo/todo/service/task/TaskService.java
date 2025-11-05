package todo.todo.service.task;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import todo.todo.dto.request.task.AddTaskBaseReq;
import todo.todo.dto.request.task.UpdateTaskReq;
import todo.todo.dto.response.task.TaskDetailRes;


@Service
public interface TaskService {
    TaskDetailRes createTask(AddTaskBaseReq req, int currentUserId);

    TaskDetailRes updateTask(int taskId, UpdateTaskReq req, int currentUserId);

    TaskDetailRes getTask(int taskId);

    String deleteTask(int taskId, int currentUserId); // soft delete

    List<TaskDetailRes> listByTeam(int teamId, boolean includeDeleted);

    List<TaskDetailRes> search(Integer teamId, Integer assigneeId, String status,
            String priority, LocalDate dueFrom, LocalDate dueTo);
}