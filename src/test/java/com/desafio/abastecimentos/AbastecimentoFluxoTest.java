package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AbastecimentoFluxoTest extends ApiTestBase {

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

    @Test
    void bombaInexistenteRetorna404() throws Exception {
        mvc.perform(post("/abastecimentos").contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(999999, "2026-09-01T17:40:00", "10")))
            .andExpect(status().isNotFound());
    }

    @Test
    void combustivelInvalidoRetorna400() throws Exception {
        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"precoPorLitro\":-1}"))
            .andExpect(status().isBadRequest());
    }
}
