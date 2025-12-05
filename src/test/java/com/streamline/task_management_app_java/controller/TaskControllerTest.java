package com.streamline.task_management_app_java.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.streamline.task_management_app_java.controller.dto.TaskCreateRequest;
import com.streamline.task_management_app_java.controller.dto.TaskResponse;
import com.streamline.task_management_app_java.controller.dto.TaskUpdateRequest;
import com.streamline.task_management_app_java.domain.Priority;
import com.streamline.task_management_app_java.domain.Status;
import com.streamline.task_management_app_java.service.TaskService;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @DisplayName("작업 생성 요청이 오면, 작업을 생성하고 ID를 반환한다.")
    @Test
    void createTask_returnsCreatedId() throws Exception {
        // Given
        Long projectId = 1L;
        TaskCreateRequest request = new TaskCreateRequest("New Task", Status.TODO, Priority.HIGH, LocalDateTime.now(),
                projectId);
        Long createdTaskId = 100L;

        given(taskService.createTask(any(), any(), any(), any(), eq(projectId))).willReturn(createdTaskId);

        // When & Then
        mockMvc.perform(post("/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(createdTaskId)));

        then(taskService).should().createTask(request.name(), request.status(), request.priority(), request.dueDate(),
                request.projectId());
    }

    @DisplayName("ID로 작업을 조회하면, 작업 정보를 반환한다.")
    @Test
    void getTask_returnsTaskResponse() throws Exception {
        // Given
        Long taskId = 100L;
        TaskResponse response = new TaskResponse(taskId, "My Task", Status.IN_PROGRESS, Priority.MEDIUM,
                LocalDateTime.now());

        given(taskService.getTask(taskId)).willReturn(response);

        // When & Then
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(taskId))
                .andExpect(jsonPath("$.data.name").value("My Task"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        then(taskService).should().getTask(taskId);
    }

    @DisplayName("작업 삭제 요청이 오면, 작업을 삭제한다.")
    @Test
    void deleteTask_deletesTask() throws Exception {
        // Given
        Long taskId = 100L;

        // When & Then
        mockMvc.perform(delete("/task/{id}", taskId))
                .andExpect(status().isOk());

        then(taskService).should().deleteTask(taskId);
    }

    @DisplayName("작업 수정 요청이 오면, 작업을 수정한다.")
    @Test
    void updateTask_updatesTask() throws Exception {
        // Given
        Long taskId = 100L;
        TaskUpdateRequest request = new TaskUpdateRequest("Updated Name", Priority.LOW, LocalDateTime.now());

        // When & Then
        mockMvc.perform(put("/task/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        then(taskService).should().updateTask(eq(taskId), any(TaskUpdateRequest.class));
    }
}
