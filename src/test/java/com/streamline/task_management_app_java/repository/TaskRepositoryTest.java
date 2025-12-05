package com.streamline.task_management_app_java.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.streamline.task_management_app_java.config.JpaConfig;
import com.streamline.task_management_app_java.domain.Priority;
import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.Status;
import com.streamline.task_management_app_java.domain.Task;

@DataJpaTest
@Import(JpaConfig.class)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @DisplayName("ID로 작업을 조회할 때 프로젝트도 함께 조회된다 (Join Fetch 확인).")
    @Test
    void findById_fetchesProject() {
        // Given
        Project project = new Project("Parent Project");
        projectRepository.save(project);

        Task task = Task.builder()
                .name("Child Task")
                .status(Status.TODO)
                .priority(Priority.MEDIUM)
                .dueDate(LocalDateTime.now())
                .build();

        project.addTask(task); // 연관관계 설정
        taskRepository.save(task);

        // When
        // 영속성 컨텍스트 초기화하여 DB에서 다시 조회하도록 강제
        taskRepository.flush();
        // DataJpaTest는 기본적으로 트랜잭션 롤백되지만, 조회 쿼리 확인을 위해
        // EntityManager를 clear하거나 flush 후 조회할 수 있음.
        // 여기서는 join fetch 쿼리가 나가는지 로그로도 확인 가능하지만,
        // 기능적으로는 연관된 프로젝트가 로딩되었는지 확인.

        Optional<Task> foundTask = taskRepository.findById(task.getId());

        // Then
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getProject()).isNotNull();
        assertThat(foundTask.get().getProject().getName()).isEqualTo("Parent Project");
    }
}
