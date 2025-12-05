package com.streamline.task_management_app_java.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.streamline.task_management_app_java.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t join fetch t.project where t.id = :id")
    Optional<Task> findById(Long id);
}
