package io.github.agpaluch.reports;

import io.github.agpaluch.model.event.TaskDone;
import io.github.agpaluch.model.event.TaskEvent;
import io.github.agpaluch.model.event.TaskUndone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
class ChangedTaskEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ChangedTaskEventListener.class);

    private final PersistedTaskEventRepository repository;

    ChangedTaskEventListener(final PersistedTaskEventRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener
    public void on(TaskDone event){
        onChanged(event);
    }
    @Async
    @EventListener
    public void on(TaskUndone event){
        onChanged(event);
    }


    public void onChanged(final TaskEvent event) {
        logger.info("Got " + event);
        repository.save(new PersistedTaskEvent(event));
    }

}
