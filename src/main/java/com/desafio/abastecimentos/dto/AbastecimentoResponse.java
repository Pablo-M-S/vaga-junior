package com.desafio.abastecimentos.dto;

import com.desafio.abastecimentos.model.Abastecimento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Abastecimento devolvido pela API. O precoPorLitro é o preço praticado no
 * momento do abastecimento (não muda se o combustível for reajustado depois).
 */
public record AbastecimentoResponse(
        Long id,
        BombaResponse bomba,
        LocalDateTime data,
        BigDecimal litros,
        BigDecimal precoPorLitro,
        BigDecimal valorTotal) {

    public static AbastecimentoResponse de(Abastecimento a) {
        return new AbastecimentoResponse(a.getId(), BombaResponse.de(a.getBomba()), a.getData(),
            a.getLitros(), a.getPrecoPorLitro(), a.getValorTotal());
    }
}
