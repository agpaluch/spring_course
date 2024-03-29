package io.github.agpaluch.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Project's description must not be empty")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="project")
    private Set<TaskGroup> taskGroups;


    @OneToMany(cascade = CascadeType.ALL, mappedBy="project")
    private Set<ProjectStep> steps;

    public Project() {
    }

    public int getId() {
        return id;
    }

    void setId(final int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    Set<TaskGroup> getTaskGroups() {
        return taskGroups;
    }

    void setTaskGroups(final Set<TaskGroup> taskGroups) {
        this.taskGroups = taskGroups;
    }

    public Set<ProjectStep> getSteps() {
        return steps;
    }

    public void setSteps(final Set<ProjectStep> projectSteps) {
        this.steps = projectSteps;
    }
}
