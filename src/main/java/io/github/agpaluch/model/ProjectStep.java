package io.github.agpaluch.model;

import javax.persistence.*;

@Entity
@Table(name = "project_steps")
public class ProjectStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    private long daysToDeadline;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public String getDescription() {
        return description;
    }

    public long getDaysToDeadline() {
        return daysToDeadline;
    }
}
