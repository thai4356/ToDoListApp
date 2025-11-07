package todo.todo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    // Tạo subtask (dùng AddSubTaskReq)
    @PostMapping
    public ResponseEntity<SubtaskDetailRes> create(@RequestBody AddSubTaskReq req) {
        // AddSubTaskReq: { taskId, title, orderIndex (optional - service tự set) }
        return ResponseEntity.ok(subTaskService.create(req.getTaskId(), req.getTitle()));
    }

    // Cập nhật subtask (dùng UpdateSubTaskReq)
    @PatchMapping("/{subTaskId}")
    public ResponseEntity<SubtaskDetailRes> update(@PathVariable int subTaskId,
            @RequestBody UpdateSubTaskReq req) {
        SubtaskDetailRes res = null;

        // đổi tiêu đề nếu có
        if (req.getTitle() != null && !req.getTitle().trim().isEmpty()) {
            res = subTaskService.rename(subTaskId, req.getTitle().trim());
        }

        // set done/undone nếu có
        if (req.getIsDone() != null) {
            res = subTaskService.setDone(subTaskId, req.getIsDone());
        }

        // orderIndex: controller này KHÔNG xử lý reorder đơn lẻ.
        // Dùng endpoint riêng /task/{taskId}/reorder với danh sách id theo thứ tự.

        return ResponseEntity.ok(res);
    }

    // Xoá mềm subtask
    @DeleteMapping("/{subTaskId}")
    public ResponseEntity<Void> delete(@PathVariable int subTaskId) {
        boolean ok = subTaskService.softDelete(subTaskId);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Reorder theo danh sách id (tuỳ chọn)
    @PatchMapping("/task/{taskId}/reorder")
    public ResponseEntity<List<SubtaskDetailRes>> reorder(@PathVariable int taskId,
            @RequestBody List<Integer> orderedSubTaskIds) {
        return ResponseEntity.ok(subTaskService.reorder(taskId, orderedSubTaskIds));
    }
}