package io.github.agpaluch.logic;

import io.github.agpaluch.TaskConfigurationProperties;
import io.github.agpaluch.model.*;
import io.github.agpaluch.model.projection.GroupReadModel;
import io.github.agpaluch.model.projection.GroupTaskReadModel;
import io.github.agpaluch.model.projection.GroupWriteModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    ProjectService(final ProjectRepository repository, final TaskGroupRepository taskGroupRepository, final TaskConfigurationProperties config) {
        this.projectRepository = repository;
        this.taskGroupRepository = taskGroupRepository;
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

        TaskGroup result = projectRepository.findById(projectId).map(
            project ->{
                TaskGroup taskGroup = new TaskGroup();
                taskGroup.setDescription(project.getDescription());
                taskGroup.setTasks(project.getProjectSteps().stream()
                        .map(step -> {
                            LocalDateTime taskDeadline = deadline.plusDays(step.getDaysToDeadline());
                            return new Task(step.getDescription(), taskDeadline);
                        }).collect(Collectors.toSet()));
            return taskGroup;
            }
        ).orElseThrow(() -> new IllegalArgumentException("Project with given id not found."));

        return new GroupReadModel(result);

    }
}
