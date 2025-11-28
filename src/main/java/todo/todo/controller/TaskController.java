package todo.todo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import todo.todo.dto.request.task.AddTaskBaseReq;
import todo.todo.dto.request.task.UpdateTaskReq;
import todo.todo.dto.response.task.TaskDetailRes;
import todo.todo.service.task.TaskService;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Create
    @PostMapping
    public ResponseEntity<TaskDetailRes> create(
            @Valid @RequestBody AddTaskBaseReq req,
            @RequestParam int currentUserId) {
        return ResponseEntity.ok(taskService.createTask(req, currentUserId));
    }

    // Update (partial)
    @PatchMapping("/{id}")
    public ResponseEntity<TaskDetailRes> update(
            @PathVariable int id,
            @Valid @RequestBody UpdateTaskReq req,
            @RequestParam int currentUserId) {
        return ResponseEntity.ok(taskService.updateTask(id, req, currentUserId));
    }

    // Get by id
    @GetMapping("/{id}")
    public ResponseEntity<TaskDetailRes> get(@PathVariable int id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    // Delete (soft)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id, @RequestParam int currentUserId) {
        String msg = taskService.deleteTask(id, currentUserId);
        return ResponseEntity.ok(msg);
    }

    @GetMapping
    public ResponseEntity<List<TaskDetailRes>> listByTeam(
            @RequestParam int teamId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        return ResponseEntity.ok(taskService.listByTeam(teamId, includeDeleted));
    }

    
    // Search (optional params)
    @GetMapping("/search")
    public ResponseEntity<List<TaskDetailRes>> search(
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) Integer assigneeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo) {
        return ResponseEntity.ok(taskService.search(teamId, assigneeId, status, priority, dueFrom, dueTo));
    }
}
