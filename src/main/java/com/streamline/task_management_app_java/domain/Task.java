package com.streamline.task_management_app_java.domain;

import com.streamline.task_management_app_java.controller.dto.TaskUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Task extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private Priority priority;

  private LocalDateTime dueDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Builder
  public Task(String name, Status status, Priority priority, LocalDateTime dueDate) {
    this.name = name;
    this.status = status;
    this.priority = priority;
    this.dueDate = dueDate;
  }

  protected void assignProject(Project project) {
    this.project = project;
  }

  protected void unassignProject() {
    this.project = null;
  }

  public void updateTask(TaskUpdateRequest request) {
    name = request.name();
    priority = request.priority();
    dueDate = request.dueDate();
    status = request.status();
  }
}
