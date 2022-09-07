package io.github.agpaluch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.agpaluch.model.Task;
import io.github.agpaluch.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repo;

    @Test
    void httpGet_returnsGivenTask() throws Exception {
        //given
        int id = repo.save(new Task("foo", LocalDateTime.now())).getId();

        //when + then
        mockMvc.perform(get("/tasks/"+id))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void httpPost_createsTask() throws Exception {
        //given
        Task toCreate = new Task("foo", LocalDateTime.now());
        ObjectMapper objectMapper = getObjectMapperWithCorrectDateSerialization();

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .content(objectMapper.writeValueAsString(toCreate))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("description", is("foo")))
                .andExpect(jsonPath("deadline", is(toCreate.getDeadline().toString())));
    }

    @Test
    void httpPut_updatesTask() throws Exception {
        //given
        Task taskInRepo = repo.save(new Task("foo", LocalDateTime.now()));
        int idToUpdate = taskInRepo.getId();
        String newDescription = "bar";
        Task taskAfterUpdate = new Task(newDescription, LocalDateTime.now());
        ObjectMapper objectMapper = getObjectMapperWithCorrectDateSerialization();

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/"+idToUpdate)
                .content(objectMapper.writeValueAsString(taskAfterUpdate))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Task updatedTaskFromRepo = repo.findById(idToUpdate).orElse(null);

        assertThat(updatedTaskFromRepo).isNotNull();
        assertThat(updatedTaskFromRepo.getDescription()).isEqualTo(newDescription);
        assertThat(updatedTaskFromRepo.getDeadline()).isEqualTo(taskAfterUpdate.getDeadline());
    }

    @Test
    void httpPut_updatingNonExistingTask_returnsStatusNotFound() throws Exception {
        //given
        int maximumId = repo.findAll().stream().mapToInt(Task::getId).max().orElse(0);
        int idToUpdate = maximumId + 1;
        Task taskToUpdate = new Task("foo", LocalDateTime.now());
        try {
            var field = Task.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(taskToUpdate, idToUpdate);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper objectMapper = getObjectMapperWithCorrectDateSerialization();


        //when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/" + idToUpdate)
                .content(objectMapper.writeValueAsString(taskToUpdate))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

        private ObjectMapper getObjectMapperWithCorrectDateSerialization() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

}
