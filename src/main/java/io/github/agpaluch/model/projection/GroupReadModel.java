package io.github.agpaluch.model.projection;

import io.github.agpaluch.model.Task;
import io.github.agpaluch.model.TaskGroup;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupReadModel {
    private int id;

    @NotBlank(message = "Task group's description must not be empty")
    private String description;
    /**
     * Deadline from the latest task in group
     */
    private LocalDateTime deadline;

    private @Valid Set<GroupTaskReadModel> tasks;

    public GroupReadModel(TaskGroup source) {
        id = source.getId();
        description = source.getDescription();
        source.getTasks().stream()
                .map(Task::getDeadline)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresent(date -> deadline = date);
        tasks = source.getTasks().stream()
                .map(GroupTaskReadModel::new)
                .collect(Collectors.toSet());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public @Valid Set<GroupTaskReadModel> getTasks() {
        return tasks;
    }

    public void setTasks(final @Valid Set<GroupTaskReadModel> tasks) {
        this.tasks = tasks;
    }

    public int getId() {
        return id;
    }

    void setId(final int id) {
        this.id = id;
    }
}
