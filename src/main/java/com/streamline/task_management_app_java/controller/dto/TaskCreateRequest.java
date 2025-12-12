package com.streamline.task_management_app_java.controller.dto;

import com.streamline.task_management_app_java.domain.Priority;
import com.streamline.task_management_app_java.domain.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TaskCreateRequest(
    @NotBlank String name,
    @NotNull Status status,
    @NotNull Priority priority,
    @NotNull LocalDateTime dueDate,
    @NotNull Long projectId) {}
