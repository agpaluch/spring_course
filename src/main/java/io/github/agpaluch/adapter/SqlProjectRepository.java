package io.github.agpaluch.adapter;

import io.github.agpaluch.model.Project;
import io.github.agpaluch.model.ProjectRepository;
import io.github.agpaluch.model.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SqlProjectRepository extends ProjectRepository, JpaRepository<Project, Integer> {

    @Override
    @Query("select distinct p from Project p join fetch p.projectSteps")
    List<Project> findAll();
}
