package io.github.agpaluch.logic;

import io.github.agpaluch.TaskConfigurationProperties;
import io.github.agpaluch.model.*;
import io.github.agpaluch.model.projection.GroupReadModel;
import io.github.agpaluch.model.projection.GroupTaskWriteModel;
import io.github.agpaluch.model.projection.GroupWriteModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;
    private TaskGroupService taskGroupService;

    ProjectService(final ProjectRepository repository, final TaskGroupRepository taskGroupRepository, final TaskGroupService taskGroupService, final TaskConfigurationProperties config) {
        this.projectRepository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }

    public List<Project> readAll(){
        return projectRepository.findAll();
    }

    public Project save(Project toSave){
        return projectRepository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId){

        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)){
            throw new IllegalStateException("Only one undone group from project is allowed.");
        }

        return projectRepository.findById(projectId).map(
            project ->{
                var targetGroup = new GroupWriteModel();
                targetGroup.setDescription(project.getDescription());
                targetGroup.setTasks(project.getProjectSteps().stream()
                        .map(step -> {
                            LocalDateTime taskDeadline = deadline.plusDays(step.getDaysToDeadline());
                            var task = new GroupTaskWriteModel();
                            task.setDescription(step.getDescription());
                            task.setDeadline(taskDeadline);
                            return task;
                        }).collect(Collectors.toSet()));
                return taskGroupService.createGroup(targetGroup);
            }
        ).orElseThrow(() -> new IllegalArgumentException("Project with given id not found."));

    }
}
