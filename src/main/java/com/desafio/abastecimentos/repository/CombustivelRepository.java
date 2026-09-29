package com.desafio.abastecimentos.repository;

import com.desafio.abastecimentos.model.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso ao banco para combustíveis. O JpaRepository já traz salvar, buscar,
 * listar e apagar; as consultas abaixo o Spring monta a partir do nome do método.
 */
public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    /** Usado no PUT: outro combustível (id diferente) já tem esse nome? */
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
