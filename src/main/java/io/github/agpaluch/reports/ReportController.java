package io.github.agpaluch.reports;

import io.github.agpaluch.model.Task;
import io.github.agpaluch.model.TaskRepository;
import io.github.agpaluch.model.event.TaskDone;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reports")
class ReportController {
    private final TaskRepository taskRepository;
    private final PersistedTaskEventRepository eventRepository;

    ReportController(final TaskRepository taskRepository, final PersistedTaskEventRepository eventRepository) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/count/{id}")
    ResponseEntity<TaskWithChangesCount> readTasksWithCount(@PathVariable int id){
        return taskRepository.findById(id)
                .map(task -> new TaskWithChangesCount(task, eventRepository.findByTaskId(id)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doneBeforeDeadline/{id}")
    ResponseEntity<TaskDoneBeforeDeadline> readTaskDoneBeforeDeadline(@PathVariable int id){
        return taskRepository.findById(id)
                .map(task -> new TaskDoneBeforeDeadline(task, eventRepository.findByTaskId(id)))
                .filter(t -> t.doneBeforeDeadline)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doneBeforeDeadline")
    ResponseEntity<List<TaskDoneBeforeDeadline>> readTasksDoneBeforeDeadline(){
        List<TaskDoneBeforeDeadline> tasksDoneBeforeDeadline = taskRepository.findByDone(true)
                .stream()
                .map(task -> new TaskDoneBeforeDeadline(task, eventRepository.findAll()))
                .filter(t -> t.doneBeforeDeadline)
                .collect(Collectors.toList());

                if (tasksDoneBeforeDeadline.isEmpty()){
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(tasksDoneBeforeDeadline);
                }

    }

    private static class TaskWithChangesCount {
        public String description;
        public boolean done;
        public int changesCount;

        TaskWithChangesCount(final Task task, final List<PersistedTaskEvent> events) {
            description = task.getDescription();
            done = task.isDone();
            changesCount = events.size();
        }
    }

    private static class TaskDoneBeforeDeadline {
        public String description;
        public boolean done;
        public LocalDateTime deadline;
        public boolean doneBeforeDeadline;


        TaskDoneBeforeDeadline(final Task task, final List<PersistedTaskEvent> events) {
            description = task.getDescription();
            deadline = task.getDeadline();
            done = task.isDone();
            if (deadline == null){
                doneBeforeDeadline = true;
            }
            if (deadline != null && done){
                LocalDateTime lastDoneDate = events.stream()
                        .filter(e -> e.getName().equals(TaskDone.class.getSimpleName()))
                        .map(PersistedTaskEvent::getOccurrence)
                        .max(Comparator.naturalOrder())
                        .orElse(null);
                doneBeforeDeadline = lastDoneDate != null && lastDoneDate.isBefore(deadline);
            }

        }
    }
}
