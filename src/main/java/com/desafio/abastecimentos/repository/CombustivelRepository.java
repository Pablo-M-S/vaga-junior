package com.desafio.abastecimentos.repository;

import com.desafio.abastecimentos.model.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {
}
