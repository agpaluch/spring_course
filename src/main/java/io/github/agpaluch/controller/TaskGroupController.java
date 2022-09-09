package io.github.agpaluch.controller;


import io.github.agpaluch.logic.TaskGroupService;
import io.github.agpaluch.model.Task;
import io.github.agpaluch.model.TaskGroup;
import io.github.agpaluch.model.TaskRepository;
import io.github.agpaluch.model.projection.GroupReadModel;
import io.github.agpaluch.model.projection.GroupWriteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
class TaskGroupController {
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;
    private final TaskRepository taskRepository;

    TaskGroupController(final TaskGroupService service, final TaskRepository taskRepository) {
        this.service = service;
        this.taskRepository = taskRepository;
    }

    @GetMapping
    ResponseEntity<List<GroupReadModel>> readAllGroups(){
        return ResponseEntity.ok(service.readAll());
    }

    @GetMapping("/{id}/tasks")
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id){
        if (!taskRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskRepository.findAllByGroup_Id(id));
    }

    @PostMapping
    ResponseEntity<GroupReadModel> createGroup(@RequestBody GroupWriteModel toCreate){
        GroupReadModel createdGroup = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + createdGroup.getId())).body(createdGroup);
    }

    @Transactional
    @PatchMapping("/{id}")
    ResponseEntity<?> toogleGroup(@PathVariable int id){
        service.toogleGroup(id);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalState(IllegalStateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }




}
