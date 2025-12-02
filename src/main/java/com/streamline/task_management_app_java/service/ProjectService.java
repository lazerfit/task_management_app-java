package com.streamline.task_management_app_java.service;

import com.streamline.task_management_app_java.controller.dto.ProjectCreateRequest;
import com.streamline.task_management_app_java.controller.dto.ProjectResponse;
import com.streamline.task_management_app_java.controller.dto.ProjectUpdateRequest;
import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id) {
        return ProjectResponse.of(projectRepository.findById(id).orElseThrow());
    }

    @Transactional
    public void createProject(ProjectCreateRequest request) {
        projectRepository.save(request.toEntity(request));
    }

    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Transactional
    public void updateProject(Long id, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(id).orElseThrow();
        project.updateName(request.name());
        project.updateStatus(request.status());
    }

}
