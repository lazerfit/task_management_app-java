package com.streamline.task_management_app_java.domain;

public enum ProjectStatus {
  TODO,
  IN_PROGRESS,
  DONE;

  public static ProjectStatus from(String type) {
    try {
      return ProjectStatus.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      return ProjectStatus.IN_PROGRESS;
    }
  }
}
