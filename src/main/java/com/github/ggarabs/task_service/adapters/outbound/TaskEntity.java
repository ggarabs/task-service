package com.github.ggarabs.task_service.adapters.outbound;

import java.math.BigDecimal;
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
@Table(name = "Task")
@Getter
@Setter
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID idemployer;
    private String title;
    private String description;
    private String city;
    private String neighborhood ;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime dateTimeOpen;
    private LocalDateTime dateTimeClosed;
    private LocalDateTime dateTimeCriation;
    private BigDecimal income;
    private TaskModality modality;
}
