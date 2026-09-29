package com.desafio.abastecimentos.repository;

import com.desafio.abastecimentos.model.Bomba;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso ao banco para bombas. O JpaRepository já traz salvar, buscar,
 * listar e apagar; as consultas abaixo o Spring monta a partir do nome do método.
 */
public interface BombaRepository extends JpaRepository<Bomba, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    /** Usado no PUT: outra bomba (id diferente) já tem esse nome? */
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
