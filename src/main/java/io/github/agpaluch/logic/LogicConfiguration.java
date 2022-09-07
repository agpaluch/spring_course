package io.github.agpaluch.logic;

import io.github.agpaluch.TaskConfigurationProperties;
import io.github.agpaluch.model.ProjectRepository;
import io.github.agpaluch.model.TaskGroupRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LogicConfiguration {

    @Bean
    ProjectService service(
            final ProjectRepository projectRepository,
            final TaskGroupRepository taskGroupRepository,
            final TaskGroupService taskGroupService,
            final TaskConfigurationProperties config
            ){
        return new ProjectService(projectRepository, taskGroupRepository, taskGroupService, config);
    }
}
