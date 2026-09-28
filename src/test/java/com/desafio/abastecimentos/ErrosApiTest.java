package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ErrosApiTest extends ApiTestBase {

    @Test
    void apagarCombustivelComBombaRetorna409() throws Exception {
        int combustivelId = criarCombustivel("Etanol", "3.99");
        criarBomba("Bomba E", combustivelId);

        mvc.perform(delete("/combustiveis/" + combustivelId))
            .andExpect(status().isConflict())
            .andExpect(status().reason("Não é possível apagar: o registro está vinculado a outros dados"));
    }

    @Test
    void apagarBombaComAbastecimentoRetorna409() throws Exception {
        int combustivelId = criarCombustivel("Diesel", "6.10");
        int bombaId = criarBomba("Bomba D", combustivelId);
        criarAbastecimento(bombaId, "2026-09-01T10:00:00", "10");

        mvc.perform(delete("/bombas/" + bombaId))
            .andExpect(status().isConflict());
    }

    @Test
    void validacaoInformaQualCampoFalhou() throws Exception {
        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"precoPorLitro\":-1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.nome").exists())
            .andExpect(jsonPath("$.campos.precoPorLitro").exists());
    }

    @Test
    void casasDecimaisEValoresEnormesRetornam400() throws Exception {
        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(nomeUnico("Gasolina"), "4.12345")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.precoPorLitro").exists());

        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(nomeUnico("Gasolina"), "99999999.99")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.precoPorLitro").exists());
    }

    @Test
    void parametrosInvalidosRetornam400NoFormatoPadrao() throws Exception {
        mvc.perform(get("/abastecimentos").param("sort", "foo"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));

        mvc.perform(get("/abastecimentos").param("de", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));

        mvc.perform(get("/abastecimentos/abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }
}
