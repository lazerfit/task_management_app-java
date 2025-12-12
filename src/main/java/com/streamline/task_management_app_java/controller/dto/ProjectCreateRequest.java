package com.streamline.task_management_app_java.controller.dto;

import com.streamline.task_management_app_java.domain.Project;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public record ProjectCreateRequest(@NotBlank(message = "Name is mandatory") String name) {

  public Project toEntity(@Nonnull ProjectCreateRequest request) {
    return new Project(request.name());
  }
}
