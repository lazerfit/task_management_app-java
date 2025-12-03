package com.streamline.task_management_app_java.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import com.streamline.task_management_app_java.controller.dto.ProjectCreateRequest;
import com.streamline.task_management_app_java.controller.dto.ProjectResponse;
import com.streamline.task_management_app_java.controller.dto.ProjectUpdateRequest;
import com.streamline.task_management_app_java.domain.Status;
import com.streamline.task_management_app_java.service.ProjectService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @DisplayName("ID로 프로젝트를 조회하면, 프로젝트 정보를 반환한다.")
    @Test
    void getProject_withValidId_returnsProjectResponse() throws Exception {
        // Given
        Long projectId = 1L;
        ProjectResponse projectResponse = new ProjectResponse(LocalDateTime.now(), projectId,
            "Test Project", Status.IN_PROGRESS);
        given(projectService.getProject(projectId)).willReturn(projectResponse);

        // When & Then
        mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(projectId))
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @DisplayName("프로젝트 생성 요청이 오면, 프로젝트를 생성한다.")
    @Test
    void createProject_withValidRequest_createsProject() throws Exception {
        // Given
        ProjectCreateRequest createRequest = new ProjectCreateRequest("New Project", Status.TODO);
        String requestBody = objectMapper.writeValueAsString(createRequest);

        // When & Then
        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        then(projectService).should().createProject(createRequest);
    }

    @DisplayName("프로젝트 삭제 요청이 오면, 프로젝트를 삭제한다.")
    @Test
    void deleteProject_withValidId_deletesProject() throws Exception {
        // Given
        Long projectId = 1L;

        // When & Then
        mockMvc.perform(delete("/project/{id}", projectId))
                .andExpect(status().isOk());

        then(projectService).should().deleteProject(projectId);
    }

    @DisplayName("프로젝트 수정 요청이 오면, 프로젝트를 수정한다.")
    @Test
    void updateProject_withValidIdAndRequest_updatesProject() throws Exception {
        // Given
        Long projectId = 1L;
        ProjectUpdateRequest updateRequest = new ProjectUpdateRequest(1L,"update", Status.COMPLETE);
        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // When & Then
        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        then(projectService).should().updateProject(projectId, updateRequest);
    }
}
