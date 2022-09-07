package io.github.agpaluch.logic;

import io.github.agpaluch.model.TaskGroup;
import io.github.agpaluch.model.TaskGroupRepository;
import io.github.agpaluch.model.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when undone tasks")
    void toogleGroup_undoneTasks_throwsIllegalStateException() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);
        var toTest = new TaskGroupService(null, mockTaskRepository);

        //when
        var exception = catchThrowable(() -> toTest.toogleGroup(anyInt()));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone tasks");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when no group with given id exists")
    void toogleGroup_groupDoesNotExist_throwsIllegalArgumentException() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);

        //and
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.empty());

        //and
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);

        //when
        var exception = catchThrowable(() -> toTest.toogleGroup(anyInt()));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should change done status of the group")
    void toogleGroup_groupExists_noUndoneTasks_tooglesGroupDoneStatus() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);

        //and
        TaskGroup taskGroup = new TaskGroup();
        boolean beforeToggle = taskGroup.isDone();

        //and
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.of(taskGroup));

        //and
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);

        //when
        toTest.toogleGroup(anyInt());

        //then
        assertThat(taskGroup).hasFieldOrPropertyWithValue("done", !beforeToggle);
    }


    private TaskRepository taskRepositoryReturning(final boolean result) {
        var mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(result);
        return mockTaskRepository;
    }

}