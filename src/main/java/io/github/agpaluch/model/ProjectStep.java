package io.github.agpaluch.model;

import javax.persistence.*;

@Entity
@Table(name = "project_steps")
class ProjectStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    private long days_to_deadline;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
