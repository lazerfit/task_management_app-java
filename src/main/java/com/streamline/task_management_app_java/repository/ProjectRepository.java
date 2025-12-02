package com.streamline.task_management_app_java.repository;

import com.streamline.task_management_app_java.domain.Project;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<@NonNull Project, @NonNull Long> {

}
