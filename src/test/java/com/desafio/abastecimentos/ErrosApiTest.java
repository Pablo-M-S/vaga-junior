package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** Erros padronizados da API: 400 (validação e parâmetros) e 409 (vínculos). */
class ErrosApiTest extends ApiTestBase {

    /** Apagar um combustível que ainda tem bomba ligada é recusado com 409. */
    @Test
    void apagarCombustivelComBombaRetorna409() throws Exception {
        int combustivelId = criarCombustivel("Etanol", "3.99");
        criarBomba("Bomba E", combustivelId);

        mvc.perform(delete("/combustiveis/" + combustivelId))
            .andExpect(status().isConflict())
            .andExpect(status().reason("Não é possível apagar: o registro está vinculado a outros dados"));
    }

    /** Apagar uma bomba que já tem abastecimentos é recusado com 409. */
    @Test
    void apagarBombaComAbastecimentoRetorna409() throws Exception {
        int combustivelId = criarCombustivel("Diesel", "6.10");
        int bombaId = criarBomba("Bomba D", combustivelId);
        criarAbastecimento(bombaId, "2026-09-01T10:00:00", "10");

        mvc.perform(delete("/bombas/" + bombaId))
            .andExpect(status().isConflict());
    }

    /** O 400 de validação lista cada campo inválido no objeto "campos". */
    @Test
    void validacaoInformaQualCampoFalhou() throws Exception {
        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"precoPorLitro\":-1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.nome").exists())
            .andExpect(jsonPath("$.campos.precoPorLitro").exists());
    }

    /** Preço com mais de 3 casas decimais ou acima de 7 dígitos inteiros é recusado com 400. */
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

    /** Ordenação por campo inexistente, data inválida e id não numérico dão 400 no formato padrão. */
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
