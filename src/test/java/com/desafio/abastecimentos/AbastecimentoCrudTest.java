package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** GET por id, PUT, DELETE, preço praticado e data futura em abastecimentos. */
class AbastecimentoCrudTest extends ApiTestBase {

    private static final String DATA = "2026-09-01T10:00:00";

    @Test
    void buscaPorId() throws Exception {
        int bombaId = criarBomba("Bomba 1", criarCombustivel("Gasolina", "5.89"));
        int id = criarAbastecimento(bombaId, DATA, "10");

        mvc.perform(get("/abastecimentos/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.bomba.id").value(bombaId))
            .andExpect(jsonPath("$.litros").value(10.0))
            .andExpect(jsonPath("$.valorTotal").value(58.90));
    }

    @Test
    void buscaPorIdInexistenteRetorna404() throws Exception {
        mvc.perform(get("/abastecimentos/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void atualizaLitrosERecalculaOTotal() throws Exception {
        int bombaId = criarBomba("Bomba 1", criarCombustivel("Gasolina", "5.00"));
        int id = criarAbastecimento(bombaId, DATA, "10");

        mvc.perform(put("/abastecimentos/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(bombaId, "2026-09-02T11:30:00", "20")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.litros").value(20.0))
            .andExpect(jsonPath("$.valorTotal").value(100.0))
            .andExpect(jsonPath("$.data").value("2026-09-02T11:30:00"));
    }

    @Test
    void reajusteDoCombustivelNaoAlteraOHistorico() throws Exception {
        int combustivelId = criarCombustivel("Gasolina", "5.00");
        int bombaId = criarBomba("Bomba 1", combustivelId);
        int id = criarAbastecimento(bombaId, DATA, "10");

        // o combustível sobe de preço depois do abastecimento
        mvc.perform(put("/combustiveis/" + combustivelId).contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(nomeUnico("Gasolina"), "6.00")))
            .andExpect(status().isOk());

        mvc.perform(get("/abastecimentos/" + id))
            .andExpect(jsonPath("$.precoPorLitro").value(5.0))
            .andExpect(jsonPath("$.valorTotal").value(50.0));

        // corrigir os litros mantém o preço praticado na época
        mvc.perform(put("/abastecimentos/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(bombaId, DATA, "20")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.precoPorLitro").value(5.0))
            .andExpect(jsonPath("$.valorTotal").value(100.0));

        // um abastecimento novo já usa o preço atual
        int novo = criarAbastecimento(bombaId, DATA, "10");
        mvc.perform(get("/abastecimentos/" + novo))
            .andExpect(jsonPath("$.precoPorLitro").value(6.0))
            .andExpect(jsonPath("$.valorTotal").value(60.0));
    }

    @Test
    void trocarDeBombaUsaOPrecoDoNovoCombustivel() throws Exception {
        int bombaGasolina = criarBomba("Gasolina", criarCombustivel("Gasolina", "5.00"));
        int bombaEtanol = criarBomba("Etanol", criarCombustivel("Etanol", "4.00"));
        int id = criarAbastecimento(bombaGasolina, DATA, "10");

        mvc.perform(put("/abastecimentos/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(bombaEtanol, DATA, "10")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bomba.id").value(bombaEtanol))
            .andExpect(jsonPath("$.precoPorLitro").value(4.0))
            .andExpect(jsonPath("$.valorTotal").value(40.0));
    }

    @Test
    void atualizarInexistenteRetorna404() throws Exception {
        int bombaId = criarBomba("Bomba 1", criarCombustivel("Gasolina", "5.00"));

        mvc.perform(put("/abastecimentos/999999").contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(bombaId, DATA, "10")))
            .andExpect(status().isNotFound());
    }

    @Test
    void apagaAbastecimento() throws Exception {
        int bombaId = criarBomba("Bomba 1", criarCombustivel("Gasolina", "5.00"));
        int id = criarAbastecimento(bombaId, DATA, "10");

        mvc.perform(delete("/abastecimentos/" + id))
            .andExpect(status().isNoContent());

        mvc.perform(get("/abastecimentos/" + id))
            .andExpect(status().isNotFound());

        // sem abastecimentos vinculados, a bomba já pode ser apagada
        mvc.perform(delete("/bombas/" + bombaId))
            .andExpect(status().isNoContent());
    }

    @Test
    void apagarInexistenteRetorna404() throws Exception {
        mvc.perform(delete("/abastecimentos/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void dataNoFuturoRetorna400() throws Exception {
        int bombaId = criarBomba("Bomba 1", criarCombustivel("Gasolina", "5.00"));
        String amanha = LocalDateTime.now().plusDays(1).withNano(0).toString();

        mvc.perform(post("/abastecimentos").contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(bombaId, amanha, "10")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.data").exists());

        int id = criarAbastecimento(bombaId, DATA, "10");
        mvc.perform(put("/abastecimentos/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(abastecimentoJson(bombaId, amanha, "10")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.data").exists());
    }
}
