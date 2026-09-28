package com.desafio.abastecimentos.repository;

import com.desafio.abastecimentos.model.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long> {
}
