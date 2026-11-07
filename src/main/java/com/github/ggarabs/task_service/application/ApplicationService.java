package com.github.ggarabs.task_service.application;

import com.github.ggarabs.task_service.adapters.outbound.ApplicationEntity;
import com.github.ggarabs.task_service.adapters.outbound.ApplicationRepository;
import com.github.ggarabs.task_service.adapters.outbound.ApplicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public ApplicationEntity createApplication(ApplicationEntity application) {
        if (application.getStatus() == null) {
            application.setStatus(ApplicationStatus.PENDING);
        }
        return applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationEntity> getApplicationsByTaskId(UUID taskId) {
        return applicationRepository.findByTaskId(taskId);
    }

    @Transactional(readOnly = true)
    public ApplicationEntity getApplicationById(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Candidatura não encontrada com o ID: " + id));
    }

    @Transactional
    public ApplicationEntity updateStatus(UUID id, ApplicationStatus newStatus) {
        ApplicationEntity application = getApplicationById(id);
        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }

    @Transactional
    public void deleteApplication(UUID id) {
        if (!applicationRepository.existsById(id)) {
            throw new NoSuchElementException("Candidatura não encontrada com o ID: " + id);
        }
        applicationRepository.deleteById(id);
    }
}