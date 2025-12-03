package com.streamline.task_management_app_java.domain;

import java.util.List;
import java.util.ArrayList;

import com.streamline.task_management_app_java.domain.Task;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public Project(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        task.assignProject(this);
    }
}
