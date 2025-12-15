package com.streamline.task_management_app_java.repository;

import com.streamline.task_management_app_java.domain.Project;
import com.streamline.task_management_app_java.domain.ProjectStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  @Query("select p from Project p where p.status = :status")
  Optional<Project> findAllByStatus(ProjectStatus status);
}
