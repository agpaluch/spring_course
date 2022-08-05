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

}
