package com.github.ggarabs.task_service.application;

import com.github.ggarabs.task_service.adapters.outbound.TaskEntity;
import com.github.ggarabs.task_service.adapters.outbound.TaskRepository;
import com.github.ggarabs.task_service.adapters.outbound.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskEntity createTask(TaskEntity task) {
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.OPEN);
        }
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public TaskEntity getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vaga não encontrada com o ID: " + id));
    }

    @Transactional
    public TaskEntity updateStatus(UUID id, TaskStatus newStatus) {
        TaskEntity task = getTaskById(id);
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new NoSuchElementException("Vaga não encontrada com o ID: " + id);
        }
        taskRepository.deleteById(id);
    }
}