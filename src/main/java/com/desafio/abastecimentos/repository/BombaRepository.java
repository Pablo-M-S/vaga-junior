package com.desafio.abastecimentos.repository;

import com.desafio.abastecimentos.model.Bomba;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BombaRepository extends JpaRepository<Bomba, Long> {
}
