package com.desafio.abastecimentos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Dados de entrada de um abastecimento. O valor total e o preço são calculados pela API. */
public record AbastecimentoRequest(
        @NotNull(message = "O id da bomba é obrigatório")
        Long bombaId,

        @NotNull(message = "A data é obrigatória")
        @PastOrPresent(message = "A data não pode estar no futuro")
        LocalDateTime data,

        @NotNull(message = "A quantidade de litros é obrigatória")
        @DecimalMin(value = "0.01", message = "O mínimo é 0,01 litro")
        @Digits(integer = 7, fraction = 3, message = "Use no máximo 7 dígitos inteiros e 3 casas decimais")
        BigDecimal litros) {
}
