package com.github.ggarabs.task_service.application;

import com.github.ggarabs.task_service.adapters.outbound.DuvidaVagaEntity;
import com.github.ggarabs.task_service.adapters.outbound.DuvidaVagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DuvidaVagaService {

    private final DuvidaVagaRepository duvidaRepository;

    public DuvidaVagaService(DuvidaVagaRepository duvidaRepository) {
        this.duvidaRepository = duvidaRepository;
    }

    @Transactional
    public DuvidaVagaEntity createDuvida(DuvidaVagaEntity duvida) {
        return duvidaRepository.save(duvida);
    }

    @Transactional(readOnly = true)
    public List<DuvidaVagaEntity> getDuvidasByTaskId(UUID taskId) {
        return duvidaRepository.findByTaskId(taskId);    
}

    @Transactional
    public DuvidaVagaEntity responderDuvida(UUID id, String resposta) {
        DuvidaVagaEntity duvida = duvidaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Dúvida não encontrada com o ID: " + id));
        
        duvida.setAnswer(resposta);
        duvida.setDateTimeAnswer(LocalDateTime.now());
        return duvidaRepository.save(duvida);
    }
}