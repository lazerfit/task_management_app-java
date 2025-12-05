package com.streamline.task_management_app_java.controller.dto;

import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.Status;
import java.time.LocalDateTime;

/**
 * DTO for {@link Project}
 */
public record ProjectResponse(
        LocalDateTime createdAt, Long id, String name, Status status) {

    public static ProjectResponse of(Project project) {
        return new ProjectResponse(
                project.getCreatedAt(),
                project.getId(),
                project.getName(),
                project.getStatus());
    }
}
