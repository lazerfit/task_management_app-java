package com.streamline.task_management_app_java.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamline.task_management_app_java.repository.TaskRepository;
import com.streamline.task_management_app_java.domain.Status;
import com.streamline.task_management_app_java.controller.dto.TaskResponse;
import com.streamline.task_management_app_java.controller.dto.TaskUpdateRequest;
import com.streamline.task_management_app_java.domain.Priority;
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
    public Long createTask(String name, Status status, Priority priority, LocalDateTime dueDate, Long projectId) {

        Project project = projectService.getProjectEntity(projectId);

        Task task = Task.builder()
                .name(name)
                .status(status)
                .priority(priority)
                .dueDate(dueDate)
                .build();

        project.addTask(task);

        Task savedTask = taskRepository.save(task);
        return savedTask.getId();
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find task: " + id));
        task.getProject().removeTask(task);
    }

    @Transactional
    public void updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find task: " + id));
        task.updateTask(request);
    }

}
