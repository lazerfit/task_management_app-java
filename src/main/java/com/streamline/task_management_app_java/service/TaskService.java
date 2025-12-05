package com.streamline.task_management_app_java.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamline.task_management_app_java.repository.TaskRepository;
import com.streamline.task_management_app_java.controller.dto.TaskResponse;
import com.streamline.task_management_app_java.controller.dto.TaskUpdateRequest;
import com.streamline.task_management_app_java.controller.dto.TaskCreateRequest;
import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.Task;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find task: " + id));

        return TaskResponse.of(task);
    }

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {

        Project project = projectService.getProjectEntity(request.projectId());

        Task task = Task.builder()
                .name(request.name())
                .status(request.status())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .build();

        project.addTask(task);

        Task savedTask = taskRepository.save(task);
        return TaskResponse.of(savedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find task: " + id));
        task.getProject().removeTask(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find task: " + id));
        task.updateTask(request);
        return TaskResponse.of(task);
    }

}
