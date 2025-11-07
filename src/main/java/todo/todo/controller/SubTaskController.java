package todo.todo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import todo.todo.dto.request.subTask.AddSubTaskReq;
import todo.todo.dto.request.subTask.UpdateSubTaskReq;
import todo.todo.dto.response.subTask.SubtaskDetailRes;
import todo.todo.service.subTask.SubTaskService;

@RestController
@RequestMapping("/api/v1/subtasks")
@RequiredArgsConstructor
public class SubTaskController {

    private final SubTaskService subTaskService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<SubtaskDetailRes>> listByTask(@PathVariable int taskId) {
        return ResponseEntity.ok(subTaskService.listActiveByTask(taskId));
    }

    @PostMapping
    public ResponseEntity<SubtaskDetailRes> create(@RequestBody AddSubTaskReq req) {
        if (req == null)
            return ResponseEntity.badRequest().build();
        SubtaskDetailRes res = subTaskService.create(req.getTaskId(), req.getTitle());
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubtaskDetailRes> update(
            @PathVariable int id,
            @RequestBody UpdateSubTaskReq req) {

        SubtaskDetailRes result = null;

        if (req.getTitle() != null && !req.getTitle().isEmpty()) {
            result = subTaskService.rename(id, req.getTitle());
        }

        if (req.getIsDone() != null) {
            result = subTaskService.setDone(id, req.getIsDone());
        }

        if (req.getOrderIndex() != null && result != null) {

        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/task/{taskId}/reorder")
    public ResponseEntity<List<SubtaskDetailRes>> reorder(
            @PathVariable int taskId,
            @RequestBody List<Integer> orderedIds) {
        return ResponseEntity.ok(subTaskService.reorder(taskId, orderedIds));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable int id) {
        return ResponseEntity.ok(subTaskService.softDelete(id));
    }
}