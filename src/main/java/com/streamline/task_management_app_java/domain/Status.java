package com.streamline.task_management_app_java.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    TODO,
    IN_PROGRESS,
    COMPLETE,
    CANCLED
}
