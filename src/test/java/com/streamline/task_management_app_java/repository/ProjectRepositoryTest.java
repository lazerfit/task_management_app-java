package com.streamline.task_management_app_java.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.streamline.task_management_app_java.config.JpaConfig;
import com.streamline.task_management_app_java.domain.Project;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @DisplayName("프로젝트를 저장하고 ID로 조회하면, 동일한 프로젝트가 반환된다.")
    @Test
    void saveAndFindById_returnsSavedProject() {
        // Given
        Project project = new Project("Test Project");

        // When
        Project savedProject = projectRepository.save(project);
        Optional<Project> foundProject = projectRepository.findById(savedProject.getId());

        // Then
        assertThat(foundProject).isPresent();
        assertThat(foundProject.get().getName()).isEqualTo("Test Project");
        assertThat(foundProject.get().getCreatedAt()).isNotNull(); // Auditing 동작 확인
    }

    @DisplayName("프로젝트를 삭제하면, 더 이상 조회되지 않는다.")
    @Test
    void delete_removesProject() {
        // Given
        Project project = new Project("Delete Me");
        Project savedProject = projectRepository.save(project);

        // When
        projectRepository.deleteById(savedProject.getId());
        Optional<Project> foundProject = projectRepository.findById(savedProject.getId());

        // Then
        assertThat(foundProject).isEmpty();
    }

    @DisplayName("프로젝트 정보를 수정하면, 변경된 내용이 반영된다.")
    @Test
    void update_reflectsChanges() {
        // Given
        Project project = new Project("Original Name");
        Project savedProject = projectRepository.save(project);

        // When
        savedProject.updateName("Updated Name");
        // flush()를 호출하거나 트랜잭션 내에서 조회하면 변경 감지(Dirty Checking) 동작
        Project updatedProject = projectRepository.findById(savedProject.getId()).orElseThrow();

        // Then
        assertThat(updatedProject.getName()).isEqualTo("Updated Name");
    }
}
