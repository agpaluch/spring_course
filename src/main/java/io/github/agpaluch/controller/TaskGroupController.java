package io.github.agpaluch.controller;


import io.github.agpaluch.logic.TaskGroupService;
import io.github.agpaluch.model.*;
import io.github.agpaluch.model.projection.GroupReadModel;
import io.github.agpaluch.model.projection.GroupTaskWriteModel;
import io.github.agpaluch.model.projection.GroupWriteModel;
import io.github.agpaluch.model.projection.ProjectWriteModel;
import javafx.scene.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/groups")
class TaskGroupController {
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;
    private final TaskRepository taskRepository;

    TaskGroupController(final TaskGroupService service, final TaskRepository taskRepository) {
        this.service = service;
        this.taskRepository = taskRepository;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model){
        model.addAttribute("group", new GroupWriteModel());
        return "groups";
    }

    @PostMapping(produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(@ModelAttribute("group") @Valid GroupWriteModel current,
                    BindingResult bindingResult,
                    Model model){

        if (bindingResult.hasErrors()){
            return "groups";
        }

        service.createGroup(current);
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("groups", getGroups());
        model.addAttribute("message", "Dodano grupę");
        return "groups";
    }


    @PostMapping(params = "addTask", produces = MediaType.TEXT_HTML_VALUE)
    String addGroupTask(@ModelAttribute("group") GroupWriteModel current){
        current.getTasks().add(new GroupTaskWriteModel());
        return "groups";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<List<GroupReadModel>> readAllGroups(){
        return ResponseEntity.ok(service.readAll());
    }


    @GetMapping(path = "/{id}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id){
        if (!taskRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskRepository.findAllByGroup_Id(id));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<Set<Task>> readGroup(@PathVariable int id){
        try {
            return ResponseEntity.ok(service.readById(id).getTasks());
        } catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate){
        GroupReadModel createdGroup = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + createdGroup.getId())).body(createdGroup);
    }

    @Transactional
    @PatchMapping(path = "/{id}")
    @ResponseBody
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

    @ModelAttribute("groups")
    List<GroupReadModel> getGroups(){
        return service.readAll();
    }


}
