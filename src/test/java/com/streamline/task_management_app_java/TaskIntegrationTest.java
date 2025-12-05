package com.streamline.task_management_app_java;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProjectAndTask_flow() throws Exception {
        // 1. Create Project
        String projectJson = """
            {
              "name": "Project 1",
              "status": "IN_PROGRESS"
            }
            """;
            
        mockMvc.perform(post("/v1/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));

        // 2. Create Task linked to Project 1
        String taskJson = """
            {
              "name": "Task 1",
              "status": "IN_PROGRESS",
              "priority": "HIGH",
              "projectId": 1,
              "dueDate": "2025-12-05T10:00:00"
            }
            """;

        mockMvc.perform(post("/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isOk()) // Task creation returns 200 OK per current controller
                .andExpect(jsonPath("$.data.id").exists());
    }
}
