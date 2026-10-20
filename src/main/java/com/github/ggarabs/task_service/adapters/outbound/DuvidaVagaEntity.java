package com.github.ggarabs.task_service.adapters.outbound;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "job_questions_tb")
@Getter
@Setter
public class DuvidaVagaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID idTask;

    private UUID idAuthor; 

    private String question;

    private String answer;

    private LocalDateTime dateTimeQuestion;

    private LocalDateTime dateTimeAnswer;
}
