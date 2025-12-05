package com.streamline.task_management_app_java.controller.dto;

import com.streamline.task_management_app_java.domain.Status;
import com.streamline.task_management_app_java.domain.Task;
import com.streamline.task_management_app_java.domain.Priority;
import java.time.LocalDateTime;

public record TaskResponse(Long id, String name, Status status, Priority priority, LocalDateTime dueDate) {

    public static TaskResponse of(Task task) {
        return new TaskResponse(task.getId(), task.getName(), task.getStatus(), task.getPriority(), task.getDueDate());
    }
}
