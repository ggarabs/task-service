package com.github.ggarabs.task_service.adapters.inbound;

import com.github.ggarabs.task_service.adapters.outbound.DuvidaVagaEntity;
import com.github.ggarabs.task_service.application.DuvidaVagaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/duvidas")
public class DuvidaVagaController {

    private final DuvidaVagaService duvidaService;

    public DuvidaVagaController(DuvidaVagaService duvidaService) {
        this.duvidaService = duvidaService;
    }

    @PostMapping
    public ResponseEntity<DuvidaVagaEntity> createDuvida(@RequestBody DuvidaVagaEntity duvida) {
        DuvidaVagaEntity created = duvidaService.createDuvida(duvida);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<DuvidaVagaEntity>> getDuvidasByTaskId(@PathVariable UUID taskId) {
        List<DuvidaVagaEntity> list = duvidaService.getDuvidasByTaskId(taskId);
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/responder")
    public ResponseEntity<DuvidaVagaEntity> responderDuvida(
            @PathVariable UUID id,
            @RequestBody String resposta) {
        DuvidaVagaEntity updated = duvidaService.responderDuvida(id, resposta);
        return ResponseEntity.ok(updated);
    }
}