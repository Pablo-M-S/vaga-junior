package com.desafio.abastecimentos.repository;

import com.desafio.abastecimentos.model.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Acesso ao banco para abastecimentos.
 * O JpaSpecificationExecutor permite montar a consulta com filtros opcionais
 * (bomba e período) em tempo de execução, usado em AbastecimentoService.listar.
 */
public interface AbastecimentoRepository
        extends JpaRepository<Abastecimento, Long>, JpaSpecificationExecutor<Abastecimento> {
}
