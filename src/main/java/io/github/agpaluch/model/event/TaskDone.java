package io.github.agpaluch.model.event;

import io.github.agpaluch.model.Task;

import java.time.Clock;

public class TaskDone extends TaskEvent{
    public TaskDone(final Task source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
