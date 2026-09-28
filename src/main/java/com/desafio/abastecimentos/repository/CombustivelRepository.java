package com.desafio.abastecimentos.repository;

import com.desafio.abastecimentos.model.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    /** Usado no PUT: outro combustível (id diferente) já tem esse nome? */
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
