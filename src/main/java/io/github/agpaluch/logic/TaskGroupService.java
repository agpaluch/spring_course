package io.github.agpaluch.logic;

import io.github.agpaluch.model.Project;
import io.github.agpaluch.model.TaskGroup;
import io.github.agpaluch.model.TaskGroupRepository;
import io.github.agpaluch.model.TaskRepository;
import io.github.agpaluch.model.projection.GroupReadModel;
import io.github.agpaluch.model.projection.GroupWriteModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


public class TaskGroupService {

    private TaskGroupRepository repository;
    private TaskRepository taskRepository;


    TaskGroupService(final TaskGroupRepository repository, final TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }


    public GroupReadModel createGroup(GroupWriteModel source){
        return createGroup(source, null);
    }

    GroupReadModel createGroup(GroupWriteModel source, final Project project){
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll(){
        return repository.findAll()
                .stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public TaskGroup readById(int id){
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("TaskGroup with given id not found"));
    }

    public void toogleGroup(int groupId){
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Do all the tasks first");
        }

        TaskGroup result = repository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("TaskGroup with given id not found"));
        result.setDone(!result.isDone());
        repository.save(result);
    }



}
