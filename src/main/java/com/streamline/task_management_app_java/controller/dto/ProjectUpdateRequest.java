package com.streamline.task_management_app_java.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectUpdateRequest(@NotBlank String name) {}
