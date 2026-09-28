package com.desafio.abastecimentos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Tipo de combustível (ex.: Gasolina, Etanol) com preço por litro.
 * As regras de validação da entrada ficam no CombustivelRequest;
 * aqui ficam só o mapeamento para a tabela.
 */
@Entity
public class Combustivel {

    /** Identificador gerado pelo banco (auto incremento). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome do combustível (ex.: "Gasolina"); obrigatório e único, conferido no service. */
    @Column(nullable = false)
    private String nome;

    /** Preço atual por litro. Guarda 3 casas decimais (mesmo limite do @Digits do DTO). */
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal precoPorLitro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPrecoPorLitro() { return precoPorLitro; }
    public void setPrecoPorLitro(BigDecimal precoPorLitro) { this.precoPorLitro = precoPorLitro; }
}
