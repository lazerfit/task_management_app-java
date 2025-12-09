package com.streamline.task_management_app_java.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @DisplayName("작업 생성 요청이 오면, 작업을 생성하고 반환한다.")
    @Test
    void createTask_returnsCreatedTask() throws Exception {
        // Given
        Long projectId = 1L;
        TaskCreateRequest request = new TaskCreateRequest("New Task", Status.TODO, Priority.HIGH, LocalDateTime.now(),
                projectId);
        TaskResponse response = new TaskResponse(100L, "New Task", Status.TODO, Priority.HIGH, LocalDateTime.now());

        given(taskService.createTask(any(TaskCreateRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100L));

        then(taskService).should().createTask(any(TaskCreateRequest.class));
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
        mockMvc.perform(get("/v1/task/{id}", taskId))
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
        mockMvc.perform(delete("/v1/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(taskService).should().deleteTask(taskId);
    }

    @DisplayName("작업 수정 요청이 오면, 작업을 수정하고 반환한다.")
    @Test
    void updateTask_updatesTask() throws Exception {
        // Given
        Long taskId = 100L;
        TaskUpdateRequest request = new TaskUpdateRequest("Updated Name", Priority.LOW, LocalDateTime.now(), Status.COMPLETE);
        TaskResponse response = new TaskResponse(taskId, "Updated Name", Status.COMPLETE, Priority.LOW,
                LocalDateTime.now());

        given(taskService.updateTask(eq(taskId), any(TaskUpdateRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(put("/v1/task/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.status").value("COMPLETE"));

        then(taskService).should().updateTask(eq(taskId), any(TaskUpdateRequest.class));
    }
}
