package io.github.agpaluch.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="project")
    private Set<TaskGroup> taskGroups;


    @OneToMany(cascade = CascadeType.ALL, mappedBy="project")
    private Set<ProjectStep> projectSteps;

    public String getDescription() {
        return description;
    }

    void setDescription(final String description) {
        this.description = description;
    }

    Set<TaskGroup> getTaskGroups() {
        return taskGroups;
    }

    void setTaskGroups(final Set<TaskGroup> taskGroups) {
        this.taskGroups = taskGroups;
    }

    public Set<ProjectStep> getProjectSteps() {
        return projectSteps;
    }

    void setProjectSteps(final Set<ProjectStep> projectSteps) {
        this.projectSteps = projectSteps;
    }
}
