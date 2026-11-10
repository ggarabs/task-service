package com.github.ggarabs.task_service.adapters.inbound;

import com.github.ggarabs.task_service.adapters.outbound.ApplicationEntity;
import com.github.ggarabs.task_service.adapters.outbound.ApplicationStatus;
import com.github.ggarabs.task_service.application.ApplicationService;
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
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final TaskService taskService;

    public ApplicationController(ApplicationService applicationService, TaskService taskService) {
        this.applicationService = applicationService;
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CANDIDATE', 'INTERMEDIARY')")
    public ResponseEntity<ApplicationEntity> createApplication(
            @RequestBody ApplicationEntity application, Authentication authentication) {
        application.setCandidateId(UUID.fromString(authentication.getName()));
        ApplicationEntity created = applicationService.createApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'INTERMEDIARY', 'ADMINISTRATOR')")
    public ResponseEntity<List<ApplicationEntity>> getApplicationsByTaskId(
            @PathVariable UUID taskId, Authentication authentication) {
        requireTaskOwnerOrAdmin(taskId, authentication);
        List<ApplicationEntity> list = applicationService.getApplicationsByTaskId(taskId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'CONTRACTOR', 'INTERMEDIARY', 'ADMINISTRATOR')")
    public ResponseEntity<ApplicationEntity> getApplicationById(@PathVariable UUID id, Authentication authentication) {
        ApplicationEntity application = applicationService.getApplicationById(id);
        requireApplicantOrTaskOwnerOrAdmin(application, authentication);
        return ResponseEntity.ok(application);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'INTERMEDIARY', 'ADMINISTRATOR')")
    public ResponseEntity<ApplicationEntity> updateStatus(
            @PathVariable UUID id,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {
        ApplicationEntity application = applicationService.getApplicationById(id);
        requireTaskOwnerOrAdmin(application.getTaskId(), authentication);
        ApplicationEntity updated = applicationService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'CONTRACTOR', 'INTERMEDIARY', 'ADMINISTRATOR')")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id, Authentication authentication) {
        ApplicationEntity application = applicationService.getApplicationById(id);
        requireApplicantOrTaskOwnerOrAdmin(application, authentication);
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    private void requireApplicantOrTaskOwnerOrAdmin(ApplicationEntity application, Authentication authentication) {
        if (isAdmin(authentication)
                || UUID.fromString(authentication.getName()).equals(application.getCandidateId())) return;
        requireTaskOwnerOrAdmin(application.getTaskId(), authentication);
    }

    private void requireTaskOwnerOrAdmin(UUID taskId, Authentication authentication) {
        if (isAdmin(authentication)) return;
        var task = taskService.getTaskById(taskId);
        if (!UUID.fromString(authentication.getName()).equals(task.getIdemployer())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A candidatura é de outro contratante");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMINISTRATOR"::equals);
    }
}
