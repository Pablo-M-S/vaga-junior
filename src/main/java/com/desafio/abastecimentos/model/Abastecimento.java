package com.desafio.abastecimentos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro de um abastecimento realizado em uma bomba. */
@Entity
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "bomba_id")
    private Bomba bomba;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime data;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal litros;

    /** Calculado no service: litros x preço por litro do combustível da bomba. */
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
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
