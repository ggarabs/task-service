
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
@Table(name = "vagas_tb")
@Getter
@Setter
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idVaga;

    private UUID idEmpregador;
    private String titulo;
    private String descricao;
    private String cidade;
    private String bairro;

    @Enumerated(EnumType.STRING)
    private SituacaoVaga situacao;

    private LocalDateTime dataHoraAbertura;
    private LocalDateTime dataHoraFechamento;
    private LocalDateTime dataHoraCriacao;
    private BigDecimal salario;
    private String modalidade;
}
