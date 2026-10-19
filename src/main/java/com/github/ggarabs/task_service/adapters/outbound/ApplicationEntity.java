package com.github.ggarabs.task_service.adapters.outbound;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "applications_tb")
@Getter
@Setter
public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID IdVaga;
    private UUID IdCandidato;

    private LocalDateTime dataInscricao;

    @Enumerated(EnumType.STRING)
    private StatusCandidatura status;
}
