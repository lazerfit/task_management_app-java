package com.streamline.task_management_app_java.controller.dto;

import java.time.LocalDateTime;
import com.streamline.task_management_app_java.domain.Status;
import com.streamline.task_management_app_java.domain.Priority;

public record TaskCreateRequest(String name, Status status, Priority priority, LocalDateTime dueDate, Long projectId) {
}
