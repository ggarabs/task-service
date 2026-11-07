package com.github.ggarabs.task_service.adapters.inbound;

import com.github.ggarabs.task_service.adapters.outbound.ApplicationEntity;
import com.github.ggarabs.task_service.adapters.outbound.ApplicationStatus;
import com.github.ggarabs.task_service.application.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationEntity> createApplication(@RequestBody ApplicationEntity application) {
        ApplicationEntity created = applicationService.createApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ApplicationEntity>> getApplicationsByTaskId(@PathVariable UUID taskId) {
        List<ApplicationEntity> list = applicationService.getApplicationsByTaskId(taskId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationEntity> getApplicationById(@PathVariable UUID id) {
        ApplicationEntity application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(application);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationEntity> updateStatus(
            @PathVariable UUID id,
            @RequestParam ApplicationStatus status) {
        ApplicationEntity updated = applicationService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}