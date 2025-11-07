package todo.todo.service.subTask;

import java.util.List;

import org.springframework.stereotype.Service;

import todo.todo.dto.response.subTask.SubtaskDetailRes;

@Service
public interface SubTaskService {
    List<SubtaskDetailRes> listActiveByTask(int taskId);

    SubtaskDetailRes create(int taskId, String title);

    SubtaskDetailRes rename(int subTaskId, String newTitle);

    SubtaskDetailRes setDone(int subTaskId, boolean done);

    List<SubtaskDetailRes> reorder(int taskId, List<Integer> orderedSubTaskIds);

    boolean softDelete(int subTaskId);
}
