package com.streamline.task_management_app_java.service;

import com.streamline.task_management_app_java.controller.dto.ProjectCreateRequest;
import com.streamline.task_management_app_java.controller.dto.ProjectResponse;
import com.streamline.task_management_app_java.controller.dto.ProjectUpdateRequest;
import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.ProjectStatus;
import com.streamline.task_management_app_java.repository.ProjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProjectService {

  private final ProjectRepository projectRepository;

  @Transactional(readOnly = true)
  public ProjectResponse getProject(Long id) {
    return ProjectResponse.of(getProjectEntity(id));
  }

  @Transactional(readOnly = true)
  protected Project getProjectEntity(Long id) {
    return projectRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public List<ProjectResponse> getProjects(String type) {
    if (type.toUpperCase().equals("ALL")) {
      return projectRepository.findAll().stream().map(ProjectResponse::of).toList();
    }
    ProjectStatus status = ProjectStatus.from(type);
    return projectRepository.findAllByStatus(status).stream().map(ProjectResponse::of).toList();
  }

  @Transactional
  public ProjectResponse createProject(ProjectCreateRequest request) {
    Project savedProject = projectRepository.save(request.toEntity(request));
    return ProjectResponse.of(savedProject);
  }

  @Transactional
  public void deleteProject(Long id) {
    projectRepository.deleteById(id);
  }

  @Transactional
  public ProjectResponse updateProject(Long id, ProjectUpdateRequest request) {
    Project project = projectRepository.findById(id).orElseThrow();
    project.updateProject(request.name(), request.status());
    return ProjectResponse.of(project);
  }
}
