package com.github.ggarabs.task_service.adapters.inbound;

import com.github.ggarabs.task_service.adapters.outbound.DuvidaVagaEntity;
import com.github.ggarabs.task_service.adapters.outbound.TaskEntity;
import com.github.ggarabs.task_service.application.DuvidaVagaService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DuvidaVagaController.class)
@Import(SecurityConfig.class)
class DuvidaVagaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DuvidaVagaService duvidaService;

    @MockitoBean
    private TaskService taskService;

    

    @Test
    @DisplayName("GET /duvidas/task/{taskId} deve ser público")
    void deveListarDuvidasPublicamente() throws Exception {
        UUID taskId = UUID.randomUUID();
        DuvidaVagaEntity duvida = new DuvidaVagaEntity();
        duvida.setId(UUID.randomUUID());
        duvida.setIdTask(taskId);
        duvida.setQuestion("Precisa levar ferramentas?");

        when(duvidaService.getDuvidasByTaskId(taskId)).thenReturn(List.of(duvida));

        mockMvc.perform(get("/duvidas/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Precisa levar ferramentas?"));
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "CONTRACTOR")
    @DisplayName("PATCH /duvidas/{id}/responder deve permitir resposta pelo dono da vaga")
    void deveResponderDuvidaPeloDonoDaVaga() throws Exception {
        UUID employerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID taskId = UUID.randomUUID();
        UUID duvidaId = UUID.randomUUID();

        DuvidaVagaEntity duvida = new DuvidaVagaEntity();
        duvida.setId(duvidaId);
        duvida.setIdTask(taskId);

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setIdemployer(employerId);

        DuvidaVagaEntity respondida = new DuvidaVagaEntity();
        respondida.setId(duvidaId);
        respondida.setAnswer("Sim, precisa levar.");

        when(duvidaService.getDuvidaById(duvidaId)).thenReturn(duvida);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(duvidaService.responderDuvida(eq(duvidaId), any(String.class))).thenReturn(respondida);

        mockMvc.perform(patch("/duvidas/" + duvidaId + "/responder")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Sim, precisa levar."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Sim, precisa levar."));
    }
}