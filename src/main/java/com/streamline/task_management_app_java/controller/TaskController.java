package com.streamline.task_management_app_java.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.streamline.task_management_app_java.controller.dto.*;
import com.streamline.task_management_app_java.service.TaskService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/task")
    public ResponseEntity<@NonNull Long> createTask(@RequestBody TaskCreateRequest request) {
        Long id = taskService.createTask(request.name(), request.status(), request.priority(), request.dueDate(),
                request.projectId());

        return ResponseEntity.ok(id);
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable Long id) {
        TaskResponse taskResponse = taskService.getTask(id);
        return ResponseEntity.ok(ApiResponse.success(taskResponse));
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        taskService.updateTask(id, request);
        return ResponseEntity.ok().build();
    }
}
