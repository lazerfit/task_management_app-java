package com.streamline.task_management_app_java.controller.dto;

import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.ProjectStatus;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record ProjectCreateRequest(
    @NotBlank(message = "Name is mandatory") String name, @Nullable ProjectStatus status) {

  public Project toEntity(@Nonnull ProjectCreateRequest request) {
    return new Project(
        request.name(), Objects.requireNonNullElse(request.status(), ProjectStatus.TODO));
  }
}
