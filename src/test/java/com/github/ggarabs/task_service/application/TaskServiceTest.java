package com.github.ggarabs.task_service.application;

import com.github.ggarabs.task_service.adapters.outbound.TaskEntity;
import com.github.ggarabs.task_service.adapters.outbound.TaskRepository;
import com.github.ggarabs.task_service.adapters.outbound.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Deve definir status OPEN por padrão ao criar vaga sem status")
    void deveDefinirStatusOpenPorPadrao() {
        TaskEntity task = new TaskEntity();
        task.setTitle("Jardineiro");

        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskEntity resultado = taskService.createTask(task);

        assertEquals(TaskStatus.OPEN, resultado.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Deve lançar NoSuchElementException quando vaga não for encontrada")
    void deveLancarExcecaoQuandoVagaNaoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(taskRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> taskService.getTaskById(idInexistente));
    }
}