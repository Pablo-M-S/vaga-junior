package com.desafio.abastecimentos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Dados de entrada para criar ou alterar uma bomba. */
public record BombaRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotNull(message = "O id do combustível é obrigatório")
        Long combustivelId) {
}
