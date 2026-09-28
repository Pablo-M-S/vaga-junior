package com.desafio.abastecimentos.dto;

import com.desafio.abastecimentos.model.Bomba;

/** Bomba devolvida pela API, com o combustível que ela abastece. */
public record BombaResponse(Long id, String nome, CombustivelResponse combustivel) {

    public static BombaResponse de(Bomba b) {
        return new BombaResponse(b.getId(), b.getNome(), CombustivelResponse.de(b.getCombustivel()));
    }
}
