package com.desafio.abastecimentos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Tipo de combustível (ex.: Gasolina, Etanol) com preço por litro. */
@Entity
public class Combustivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 7, fraction = 3)
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal precoPorLitro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPrecoPorLitro() { return precoPorLitro; }
    public void setPrecoPorLitro(BigDecimal precoPorLitro) { this.precoPorLitro = precoPorLitro; }
}
