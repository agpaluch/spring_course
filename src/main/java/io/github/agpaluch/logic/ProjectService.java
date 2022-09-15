package io.github.agpaluch.logic;

import io.github.agpaluch.TaskConfigurationProperties;
import io.github.agpaluch.model.*;
import io.github.agpaluch.model.projection.GroupReadModel;
import io.github.agpaluch.model.projection.GroupTaskWriteModel;
import io.github.agpaluch.model.projection.GroupWriteModel;
import io.github.agpaluch.model.projection.ProjectWriteModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class ProjectService {

    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskGroupService taskGroupService;
    private TaskConfigurationProperties config;


    ProjectService(final ProjectRepository repository, final TaskGroupRepository taskGroupRepository, final TaskGroupService taskGroupService, final TaskConfigurationProperties config) {
        this.projectRepository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }

    public List<Project> readAll(){
        return projectRepository.findAll();
    }

    public Project save(ProjectWriteModel toSave){
        return projectRepository.save(toSave.toProject());
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId){

        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)){
            throw new IllegalStateException("Only one undone group from project is allowed.");
        }

        return projectRepository.findById(projectId).map(
            project ->{
                var targetGroup = new GroupWriteModel();
                targetGroup.setDescription(project.getDescription());
                targetGroup.setTasks(project.getSteps().stream()
                        .map(step -> {
                            LocalDateTime taskDeadline = deadline.plusDays(step.getDaysToDeadline());
                            var task = new GroupTaskWriteModel();
                            task.setDescription(step.getDescription());
                            task.setDeadline(taskDeadline);
                            return task;
                        }).collect(Collectors.toList()));
                return taskGroupService.createGroup(targetGroup, project);
            }
        ).orElseThrow(() -> new IllegalArgumentException("Project with given id not found."));

    }
}
