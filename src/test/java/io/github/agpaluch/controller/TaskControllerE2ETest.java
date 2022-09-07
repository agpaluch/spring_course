package io.github.agpaluch.controller;

import io.github.agpaluch.model.Task;
import io.github.agpaluch.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TaskRepository repo;


    @Test
    void httpGet_returnsAllTasks(){
        //given
        int initial = repo.findAll().size();
        repo.save(new Task("foo", LocalDateTime.now()));
        repo.save(new Task("bar", LocalDateTime.now()));


        //when
        Task[] result = restTemplate.getForObject("http://localhost:"+port+"/tasks", Task[].class);

        //then
        assertThat(result).hasSize(initial+2);
    }

    @Test
    void httpGet_returnsGivenTask() {
        //given
        Task savedTask = repo.save(new Task("foo", LocalDateTime.now()));

        //when
        ResponseEntity<Task> responseEntity =  restTemplate.getForEntity("http://localhost:"+port+"/tasks/"+savedTask.getId(), Task.class);
        Task result = responseEntity.getBody();

        //then
        assertThat(result).hasFieldOrPropertyWithValue("deadline", savedTask.getDeadline())
                .hasFieldOrPropertyWithValue("description", savedTask.getDescription())
                .hasFieldOrPropertyWithValue("done", savedTask.isDone());

        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();

    }

}