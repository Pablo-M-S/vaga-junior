package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** GET por id, GET lista, PUT e DELETE de combustíveis. */
class CombustivelCrudTest extends ApiTestBase {

    @Test
    void buscaPorId() throws Exception {
        String nome = nomeUnico("Gasolina");
        int id = criar("/combustiveis", combustivelJson(nome, "5.89"));

        mvc.perform(get("/combustiveis/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.nome").value(nome))
            .andExpect(jsonPath("$.precoPorLitro").value(5.89));
    }

    @Test
    void buscaPorIdInexistenteRetorna404() throws Exception {
        mvc.perform(get("/combustiveis/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listaCombustiveis() throws Exception {
        criarCombustivel("Lista", "4.00");

        mvc.perform(get("/combustiveis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void atualizaCombustivel() throws Exception {
        int id = criarCombustivel("Etanol", "3.99");
        String novoNome = nomeUnico("Etanol Aditivado");

        mvc.perform(put("/combustiveis/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(novoNome, "4.25")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value(novoNome))
            .andExpect(jsonPath("$.precoPorLitro").value(4.25));

        mvc.perform(get("/combustiveis/" + id))
            .andExpect(jsonPath("$.nome").value(novoNome))
            .andExpect(jsonPath("$.precoPorLitro").value(4.25));
    }

    @Test
    void atualizarComDadosInvalidosRetorna400() throws Exception {
        int id = criarCombustivel("Etanol", "3.99");

        mvc.perform(put("/combustiveis/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"precoPorLitro\":-1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.nome").exists());
    }

    @Test
    void atualizarInexistenteRetorna404() throws Exception {
        mvc.perform(put("/combustiveis/999999").contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(nomeUnico("X"), "4.00")))
            .andExpect(status().isNotFound());
    }

    @Test
    void apagaCombustivelSemVinculo() throws Exception {
        int id = criarCombustivel("Descartavel", "4.00");

        mvc.perform(delete("/combustiveis/" + id))
            .andExpect(status().isNoContent());

        mvc.perform(get("/combustiveis/" + id))
            .andExpect(status().isNotFound());
    }

    @Test
    void apagarInexistenteRetorna404() throws Exception {
        mvc.perform(delete("/combustiveis/999999"))
            .andExpect(status().isNotFound());
    }
}
