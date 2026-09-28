package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

/** Consulta de abastecimentos: filtros por bomba e período, e paginação. */
class FiltroAbastecimentoTest extends ApiTestBase {

    /** Só o abastecimento de 20/09 entra no período de 15 a 30/09 da bomba filtrada. */
    @Test
    void filtraPorBombaEPeriodo() throws Exception {
        int combustivelId = criarCombustivel("Diesel", "6.10");
        int bombaId = criarBomba("Bomba D", combustivelId);

        criarAbastecimento(bombaId, "2026-09-10T10:00:00", "10");
        criarAbastecimento(bombaId, "2026-09-20T10:00:00", "5");

        mvc.perform(get("/abastecimentos")
                .param("bombaId", String.valueOf(bombaId))
                .param("de", "2026-09-15")
                .param("ate", "2026-09-30"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].litros").value(5.0));
    }

    /** Com size=1 vem 1 item na página, mas o total de elementos continua 2. */
    @Test
    void paginaOsResultados() throws Exception {
        int combustivelId = criarCombustivel("Etanol", "4.50");
        int bombaId = criarBomba("Bomba E", combustivelId);
        criarAbastecimento(bombaId, "2026-09-10T10:00:00", "10");
        criarAbastecimento(bombaId, "2026-09-11T10:00:00", "5");

        mvc.perform(get("/abastecimentos")
                .param("bombaId", String.valueOf(bombaId))
                .param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.page.totalElements").value(2));
    }
}
