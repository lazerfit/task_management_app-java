package com.streamline.task_management_app_java.controller.dto;

import com.streamline.task_management_app_java.domain.Priority;
import com.streamline.task_management_app_java.domain.Status;
import java.time.LocalDateTime;

public record TaskUpdateRequest(
    String name, Priority priority, LocalDateTime dueDate, Status status) {}
