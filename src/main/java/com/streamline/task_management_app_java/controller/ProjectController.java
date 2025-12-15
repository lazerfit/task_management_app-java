package com.streamline.task_management_app_java.controller;

import com.streamline.task_management_app_java.controller.dto.ApiResponse;
import com.streamline.task_management_app_java.controller.dto.ProjectCreateRequest;
import com.streamline.task_management_app_java.controller.dto.ProjectResponse;
import com.streamline.task_management_app_java.controller.dto.ProjectUpdateRequest;
import com.streamline.task_management_app_java.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ProjectController {

  private final ProjectService projectService;

  @GetMapping("/v1/project/{id}")
  public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(projectService.getProject(id)));
  }

  @GetMapping("/v1/project")
  public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(
      @RequestParam(name = "filter", defaultValue = "all") String filter) {
    return ResponseEntity.ok(ApiResponse.success(projectService.getProjects(filter)));
  }

  @PostMapping("/v1/project")
  public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
      @Valid @RequestBody ProjectCreateRequest request) {
    ProjectResponse response = projectService.createProject(request);
    return new ResponseEntity<>(ApiResponse.success(response), HttpStatusCode.valueOf(201));
  }

  @DeleteMapping("/v1/project/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable("id") Long id) {
    projectService.deleteProject(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PutMapping("/v1/project/{id}")
  public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
      @PathVariable("id") Long id, @Valid @RequestBody ProjectUpdateRequest request) {
    ProjectResponse response = projectService.updateProject(id, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
