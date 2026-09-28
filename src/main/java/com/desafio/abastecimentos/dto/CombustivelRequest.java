package com.desafio.abastecimentos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Dados de entrada para criar ou alterar um combustível. */
public record CombustivelRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotNull(message = "O preço por litro é obrigatório")
        @DecimalMin(value = "0.01", message = "O preço mínimo é 0,01")
        @Digits(integer = 7, fraction = 3, message = "Use no máximo 7 dígitos inteiros e 3 casas decimais")
        BigDecimal precoPorLitro) {
}
