package com.desafio.abastecimentos.repository;

import com.desafio.abastecimentos.model.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AbastecimentoRepository
        extends JpaRepository<Abastecimento, Long>, JpaSpecificationExecutor<Abastecimento> {
}
