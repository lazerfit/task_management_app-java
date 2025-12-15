package com.streamline.task_management_app_java.controller.dto;

import com.streamline.task_management_app_java.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectUpdateRequest(@NotBlank String name, @NotNull ProjectStatus status) {}
