package io.github.agpaluch.model.projection;

import io.github.agpaluch.model.Project;
import io.github.agpaluch.model.ProjectStep;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProjectWriteModel {

    @NotBlank(message = "Project's description must not be empty")
    private String description;

    @Valid
    private List<ProjectStep> steps = new ArrayList<>();

    public ProjectWriteModel() {
        steps.add(new ProjectStep());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<ProjectStep> getSteps() {
        return steps;
    }

    public void setSteps(final List<ProjectStep> steps) {
        this.steps = steps;
    }

    public Project toProject(){
        var result = new Project();
        result.setDescription(description);
        steps.forEach(s -> s.setProject(result));
        result.setSteps(new HashSet<>(steps));
        return result;
    }
}
