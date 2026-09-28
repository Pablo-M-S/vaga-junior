package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** Fluxo principal: cadastrar combustível, bomba e abastecimento com o total calculado. */
class AbastecimentoFluxoTest extends ApiTestBase {

    /** 10 litros a 5,89 devem resultar em valor total 58,90 e preço praticado 5,89. */
    @Test
    void calculaValorTotalDoAbastecimento() throws Exception {
        int combustivelId = criarCombustivel("Gasolina", "5.89");
        int bombaId = criarBomba("Bomba 1", combustivelId);

        mvc.perform(post("/abastecimentos").contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(bombaId, "2026-09-01T17:40:00", "10")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.valorTotal").value(58.90))
            .andExpect(jsonPath("$.precoPorLitro").value(5.89));
    }

    /** Abastecimento em bomba que não existe responde 404. */
    @Test
    void bombaInexistenteRetorna404() throws Exception {
        mvc.perform(post("/abastecimentos").contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(999999, "2026-09-01T17:40:00", "10")))
            .andExpect(status().isNotFound());
    }

    /** Combustível com nome vazio e preço negativo responde 400. */
    @Test
    void combustivelInvalidoRetorna400() throws Exception {
        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"precoPorLitro\":-1}"))
            .andExpect(status().isBadRequest());
    }
}
