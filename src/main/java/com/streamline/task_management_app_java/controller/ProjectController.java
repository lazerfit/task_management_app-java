package com.streamline.task_management_app_java.controller;

import com.streamline.task_management_app_java.controller.dto.ProjectCreateRequest;
import com.streamline.task_management_app_java.controller.dto.ProjectResponse;
import com.streamline.task_management_app_java.controller.dto.ProjectUpdateRequest;
import com.streamline.task_management_app_java.service.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/project/{id}")
    public ResponseEntity<@NonNull ProjectResponse> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @PostMapping("/project")
    public ResponseEntity<@NonNull Void> createProject(@RequestBody ProjectCreateRequest request) {
        projectService.createProject(request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/project/{id}")
    public ResponseEntity<@NonNull Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<@NonNull Void> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequest request) {
        projectService.updateProject(id, request);

        return ResponseEntity.ok().build();
    }
}
