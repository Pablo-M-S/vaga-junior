package com.desafio.abastecimentos.dto;

import com.desafio.abastecimentos.model.Combustivel;
import java.math.BigDecimal;

/** Combustível devolvido pela API. */
public record CombustivelResponse(Long id, String nome, BigDecimal precoPorLitro) {

    /** Converte a entidade Combustivel para o DTO de resposta. */
    public static CombustivelResponse de(Combustivel c) {
        return new CombustivelResponse(c.getId(), c.getNome(), c.getPrecoPorLitro());
    }
}
