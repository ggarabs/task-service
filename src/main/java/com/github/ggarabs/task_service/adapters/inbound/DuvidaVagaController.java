package com.github.ggarabs.task_service.adapters.inbound;

import com.github.ggarabs.task_service.adapters.outbound.DuvidaVagaEntity;
import com.github.ggarabs.task_service.application.DuvidaVagaService;
import com.github.ggarabs.task_service.application.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/duvidas")
public class DuvidaVagaController {

    private final DuvidaVagaService duvidaService;
    private final TaskService taskService;

    public DuvidaVagaController(DuvidaVagaService duvidaService, TaskService taskService) {
        this.duvidaService = duvidaService;
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CANDIDATE', 'INTERMEDIARY')")
    public ResponseEntity<DuvidaVagaEntity> createDuvida(
            @RequestBody DuvidaVagaEntity duvida, Authentication authentication) {
        duvida.setIdAuthor(UUID.fromString(authentication.getName()));
        DuvidaVagaEntity created = duvidaService.createDuvida(duvida);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<DuvidaVagaEntity>> getDuvidasByTaskId(@PathVariable UUID taskId) {
        List<DuvidaVagaEntity> list = duvidaService.getDuvidasByTaskId(taskId);
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/responder")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'INTERMEDIARY', 'ADMINISTRATOR')")
    public ResponseEntity<DuvidaVagaEntity> responderDuvida(
            @PathVariable UUID id,
            @RequestBody String resposta,
            Authentication authentication) {
        var duvida = duvidaService.getDuvidaById(id);
        boolean admin = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMINISTRATOR"::equals);
        if (!admin && !UUID.fromString(authentication.getName())
                .equals(taskService.getTaskById(duvida.getIdTask()).getIdemployer())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente o responsável pela vaga pode responder");
        }
        DuvidaVagaEntity updated = duvidaService.responderDuvida(id, resposta);
        return ResponseEntity.ok(updated);
    }
}
