package io.github.agpaluch.model.projection;

import io.github.agpaluch.model.Task;

import javax.validation.constraints.NotBlank;

public class GroupTaskReadModel {

    @NotBlank(message = "Task's description must not be empty")
    private String description;
    private boolean done;

    public GroupTaskReadModel(Task source) {
        description = source.getDescription();
        done = source.isDone();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(final boolean done) {
        this.done = done;
    }


}
