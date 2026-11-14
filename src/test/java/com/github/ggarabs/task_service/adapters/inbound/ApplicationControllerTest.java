package com.github.ggarabs.task_service.adapters.inbound;

import com.github.ggarabs.task_service.adapters.outbound.ApplicationEntity;
import com.github.ggarabs.task_service.adapters.outbound.ApplicationStatus;
import com.github.ggarabs.task_service.adapters.outbound.TaskEntity;
import com.github.ggarabs.task_service.application.ApplicationService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@Import(SecurityConfig.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationService applicationService;

    @MockitoBean
    private TaskService taskService;

    
    @Test
    @WithMockUser(username = "22222222-2222-2222-2222-222222222222", roles = "CANDIDATE")
    @DisplayName("POST /applications deve permitir candidatura para CANDIDATE")
    void deveCriarCandidaturaParaCandidato() throws Exception {
        UUID taskId = UUID.randomUUID();
        ApplicationEntity saved = new ApplicationEntity();
        saved.setId(UUID.randomUUID());
        saved.setTaskId(taskId);
        saved.setStatus(ApplicationStatus.PENDING);

        when(applicationService.createApplication(any(ApplicationEntity.class))).thenReturn(saved);

        mockMvc.perform(post("/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\": \"" + taskId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "CONTRACTOR")
    @DisplayName("POST /applications deve negar candidatura para CONTRACTOR")
    void deveNegarCandidaturaParaContratante() throws Exception {
        mockMvc.perform(post("/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\": \"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "99999999-9999-9999-9999-999999999999", roles = "CONTRACTOR")
    @DisplayName("GET /applications/task/{taskId} deve negar acesso se contratante não for dono da vaga")
    void deveNegarListagemParaContratanteNaoDono() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setIdemployer(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        when(taskService.getTaskById(taskId)).thenReturn(task);

        mockMvc.perform(get("/applications/task/" + taskId))
                .andExpect(status().isForbidden());
    }
}