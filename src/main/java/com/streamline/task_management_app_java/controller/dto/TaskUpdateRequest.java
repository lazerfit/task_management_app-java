package com.streamline.task_management_app_java.controller.dto;

import java.time.LocalDateTime;
import com.streamline.task_management_app_java.domain.Priority;

public record TaskUpdateRequest(String name, Priority priority, LocalDateTime dueDate) {
}
