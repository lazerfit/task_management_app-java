package com.streamline.task_management_app_java.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.streamline.task_management_app_java.controller.dto.ProjectCreateRequest;
import com.streamline.task_management_app_java.controller.dto.ProjectResponse;
import com.streamline.task_management_app_java.controller.dto.ProjectUpdateRequest;
import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.Status;
import com.streamline.task_management_app_java.repository.ProjectRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @DisplayName("ID로 프로젝트를 조회하면, 프로젝트 정보를 반환한다.")
    @Test
    void getProject_withValidId_returnsProject() {
        // Given
        Long projectId = 1L;
        Project project = new Project("Test Project", Status.TODO);
        ReflectionTestUtils.setField(project, "id", projectId);
        
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        // When
        ProjectResponse response = projectService.getProject(projectId);

        // Then
        assertThat(response.id()).isEqualTo(projectId);
        assertThat(response.name()).isEqualTo("Test Project");
        assertThat(response.status()).isEqualTo(Status.TODO);
        then(projectRepository).should().findById(projectId);
    }

    @DisplayName("존재하지 않는 ID로 프로젝트를 조회하면, 예외가 발생한다.")
    @Test
    void getProject_withInvalidId_throwsException() {
        // Given
        Long projectId = 99L;
        given(projectRepository.findById(projectId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getProject(projectId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("프로젝트 생성 요청이 오면, 프로젝트를 저장한다.")
    @Test
    void createProject_savesProject() {
        // Given
        ProjectCreateRequest request = new ProjectCreateRequest("New Project", Status.TODO);

        // When
        projectService.createProject(request);

        // Then
        then(projectRepository).should().save(any(Project.class));
    }

    @DisplayName("프로젝트 삭제 요청이 오면, 프로젝트를 삭제한다.")
    @Test
    void deleteProject_deletesProject() {
        // Given
        Long projectId = 1L;

        // When
        projectService.deleteProject(projectId);

        // Then
        then(projectRepository).should().deleteById(projectId);
    }

    @DisplayName("프로젝트 수정 요청이 오면, 프로젝트 정보를 업데이트한다.")
    @Test
    void updateProject_updatesProjectFields() {
        // Given
        Long projectId = 1L;
        Project project = new Project("Old Name", Status.TODO);
        ReflectionTestUtils.setField(project, "id", projectId);
        
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        ProjectUpdateRequest request = new ProjectUpdateRequest(projectId, "New Name", Status.IN_PROGRESS);

        // When
        projectService.updateProject(projectId, request);

        // Then
        assertThat(project.getName()).isEqualTo("New Name");
        assertThat(project.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }
}
