package com.desafio.abastecimentos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro de um abastecimento realizado em uma bomba. */
@Entity
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bomba_id")
    private Bomba bomba;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal litros;

    /**
     * Preço por litro praticado no momento do abastecimento (cópia do preço do
     * combustível). Fica nulo apenas em registros antigos, criados antes deste campo.
     */
    @Column(precision = 10, scale = 3)
    private BigDecimal precoPorLitro;

    /** Calculado no service: litros x preço praticado. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Bomba getBomba() { return bomba; }
    public void setBomba(Bomba bomba) { this.bomba = bomba; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public BigDecimal getLitros() { return litros; }
    public void setLitros(BigDecimal litros) { this.litros = litros; }
    public BigDecimal getPrecoPorLitro() { return precoPorLitro; }
    public void setPrecoPorLitro(BigDecimal precoPorLitro) { this.precoPorLitro = precoPorLitro; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
