package com.streamline.task_management_app_java.controller.dto;

import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.Status;

public record ProjectCreateRequest(String name, Status status) {

    public Project toEntity(ProjectCreateRequest request) {
        return new Project(request.name());
    }
}
