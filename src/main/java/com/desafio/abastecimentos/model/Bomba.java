package com.desafio.abastecimentos.model;

import jakarta.persistence.*;

/**
 * Bomba do posto, vinculada a um tipo de combustível.
 * Relacionamento: muitas bombas podem abastecer o mesmo combustível (N para 1).
 */
@Entity
public class Bomba {

    /** Identificador gerado pelo banco (auto incremento). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome da bomba (ex.: "Bomba 1"); obrigatório e único, conferido no service. */
    @Column(nullable = false)
    private String nome;

    /** Combustível que a bomba abastece; obrigatório (chave estrangeira combustivel_id). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "combustivel_id")
    private Combustivel combustivel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Combustivel getCombustivel() { return combustivel; }
    public void setCombustivel(Combustivel combustivel) { this.combustivel = combustivel; }
}
