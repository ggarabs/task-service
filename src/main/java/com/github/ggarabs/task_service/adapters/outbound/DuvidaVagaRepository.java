package com.github.ggarabs.task_service.adapters.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DuvidaVagaRepository extends JpaRepository<DuvidaVagaEntity, UUID> {

    @Query("SELECT d FROM DuvidaVagaEntity d WHERE d.idTask = :taskId")
    List<DuvidaVagaEntity> findByTaskId(@Param("taskId") UUID taskId);
}