package com.github.ggarabs.task_service.adapters.inbound;

import com.github.ggarabs.task_service.adapters.outbound.TaskEntity;
import com.github.ggarabs.task_service.adapters.outbound.TaskStatus;
import com.github.ggarabs.task_service.application.TaskService;
import com.github.ggarabs.task_service.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;


    @Test
    @DisplayName("GET /tasks deve ser público e retornar lista de vagas")
    void deveListarVagasPublicamente() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setId(UUID.randomUUID());
        task.setTitle("Pintor Residencial");
        task.setStatus(TaskStatus.OPEN);

        when(taskService.getAllTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Pintor Residencial"));
    }

    @Test
    @DisplayName("POST /tasks deve negar criação para usuário não autenticado")
    void deveBloquearCriacaoNaoAutenticada() throws Exception {
        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Eletricista\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "CANDIDATE")
    @DisplayName("POST /tasks deve retornar 403 quando usuário for CANDIDATE")
    void deveNegarCriacaoParaCandidato() throws Exception {
        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Eletricista\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "CONTRACTOR")
    @DisplayName("POST /tasks deve criar vaga (201) quando usuário for CONTRACTOR")
    void deveCriarVagaParaContratante() throws Exception {
        UUID employerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        TaskEntity saved = new TaskEntity();
        saved.setId(UUID.randomUUID());
        saved.setTitle("Eletricista");
        saved.setIdemployer(employerId);
        saved.setStatus(TaskStatus.OPEN);

        when(taskService.createTask(any(TaskEntity.class))).thenReturn(saved);

        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Eletricista\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Eletricista"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }
}