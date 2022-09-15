package io.github.agpaluch.logic;

import io.github.agpaluch.TaskConfigurationProperties;
import io.github.agpaluch.model.ProjectRepository;
import io.github.agpaluch.model.TaskGroupRepository;
import io.github.agpaluch.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LogicConfiguration {

    @Bean
    ProjectService projectService(
            final ProjectRepository projectRepository,
            final TaskGroupRepository taskGroupRepository,
            final TaskGroupService taskGroupService,
            final TaskConfigurationProperties config
            ){
        return new ProjectService(projectRepository, taskGroupRepository, taskGroupService, config);
    }

    @Bean
    TaskGroupService taskGroupService(
            final TaskGroupRepository taskGroupRepository,
            final TaskRepository taskRepository
            ) {
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }
}
