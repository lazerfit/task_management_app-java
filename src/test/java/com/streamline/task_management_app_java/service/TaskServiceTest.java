package com.streamline.task_management_app_java.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.streamline.task_management_app_java.controller.dto.TaskResponse;
import com.streamline.task_management_app_java.controller.dto.TaskUpdateRequest;
import com.streamline.task_management_app_java.domain.Priority;
import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.Status;
import com.streamline.task_management_app_java.domain.Task;
import com.streamline.task_management_app_java.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @DisplayName("ID로 작업을 조회하면, 작업 응답을 반환한다.")
    @Test
    void getTask_returnsTaskResponse() {
        // Given
        Long taskId = 1L;
        Task task = Task.builder()
                .name("Task")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(task, "id", taskId);

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // When
        TaskResponse response = taskService.getTask(taskId);

        // Then
        assertThat(response.id()).isEqualTo(taskId);
        assertThat(response.name()).isEqualTo("Task");
        then(taskRepository).should().findById(taskId);
    }

    @DisplayName("작업 생성 시, 프로젝트를 조회하고 작업을 저장한다.")
    @Test
    void createTask_savesTaskAndAssignsToProject() {
        // Given
        Long projectId = 1L;
        Project project = new Project("Test Project");
        given(projectService.getProjectEntity(projectId)).willReturn(project);

        Task savedTask = Task.builder()
                .name("Task")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(savedTask, "id", 10L);
        given(taskRepository.save(any(Task.class))).willReturn(savedTask);

        // When
        Long taskId = taskService.createTask("Task", Status.TODO, Priority.HIGH, LocalDateTime.now(), projectId);

        // Then
        assertThat(taskId).isEqualTo(10L);
        then(projectService).should().getProjectEntity(projectId);
        then(taskRepository).should().save(any(Task.class));

        // Verify that the task was added to the project's list (logic inside addTask)
        assertThat(project.getTasks()).hasSize(1);
    }

    @DisplayName("작업 삭제 시, 작업과 프로젝트 연결을 해제한다.")
    @Test
    void deleteTask_removesTaskFromProject() {
        // Given
        Long taskId = 10L;
        Project project = new Project("Test Project");
        Task task = Task.builder().name("Task").build();
        ReflectionTestUtils.setField(task, "id", taskId);

        // Link them initially
        project.addTask(task);

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // When
        taskService.deleteTask(taskId);

        // Then
        // orphanRemoval relies on JPA, but here we verify the domain logic:
        // 1. Task removed from Project's list
        assertThat(project.getTasks()).isEmpty();
        // 2. Task's project reference is nullified
        assertThat(task.getProject()).isNull();
    }

    @DisplayName("존재하지 않는 작업 삭제 시 예외가 발생한다.")
    @Test
    void deleteTask_withInvalidId_throwsException() {
        // Given
        Long taskId = 99L;
        given(taskRepository.findById(taskId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("작업 수정 시, 작업 정보를 업데이트한다.")
    @Test
    void updateTask_updatesTaskInfo() {
        // Given
        Long taskId = 10L;
        Task task = Task.builder().name("Old Name").priority(Priority.LOW).build();
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        TaskUpdateRequest request = new TaskUpdateRequest("New Name", Priority.HIGH, LocalDateTime.now());

        // When
        taskService.updateTask(taskId, request);

        // Then
        assertThat(task.getName()).isEqualTo("New Name");
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
    }
}
