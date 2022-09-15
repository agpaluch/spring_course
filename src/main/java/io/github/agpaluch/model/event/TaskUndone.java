package io.github.agpaluch.model.event;

import io.github.agpaluch.model.Task;

import java.time.Clock;

public class TaskUndone extends TaskEvent{
    public TaskUndone(final Task source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
