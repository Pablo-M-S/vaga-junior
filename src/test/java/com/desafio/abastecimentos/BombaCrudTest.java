package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** POST com combustível inválido, GET por id, GET lista, PUT e DELETE de bombas. */
class BombaCrudTest extends ApiTestBase {

    @Test
    void buscaPorIdComOCombustivel() throws Exception {
        int combustivelId = criarCombustivel("Gasolina", "5.89");
        String nome = nomeUnico("Bomba 1");
        int id = criar("/bombas", bombaJson(nome, combustivelId));

        mvc.perform(get("/bombas/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.nome").value(nome))
            .andExpect(jsonPath("$.combustivel.id").value(combustivelId));
    }

    @Test
    void buscaPorIdInexistenteRetorna404() throws Exception {
        mvc.perform(get("/bombas/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listaBombas() throws Exception {
        criarBomba("Lista", criarCombustivel("Lista", "4.00"));

        mvc.perform(get("/bombas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void criarComCombustivelInexistenteRetorna404() throws Exception {
        mvc.perform(post("/bombas").contentType(MediaType.APPLICATION_JSON)
                .content(bombaJson(nomeUnico("Bomba X"), 999999)))
            .andExpect(status().isNotFound());
    }

    @Test
    void criarSemCombustivelRetorna400() throws Exception {
        mvc.perform(post("/bombas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"" + nomeUnico("Bomba X") + "\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.combustivelId").exists());
    }

    @Test
    void atualizaNomeECombustivel() throws Exception {
        int gasolina = criarCombustivel("Gasolina", "5.89");
        int etanol = criarCombustivel("Etanol", "3.99");
        int id = criarBomba("Bomba 1", gasolina);
        String novoNome = nomeUnico("Bomba Etanol");

        mvc.perform(put("/bombas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(bombaJson(novoNome, etanol)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value(novoNome))
            .andExpect(jsonPath("$.combustivel.id").value(etanol));

        mvc.perform(get("/bombas/" + id))
            .andExpect(jsonPath("$.nome").value(novoNome))
            .andExpect(jsonPath("$.combustivel.id").value(etanol));
    }

    @Test
    void atualizarInexistenteRetorna404() throws Exception {
        int combustivelId = criarCombustivel("Gasolina", "5.89");

        mvc.perform(put("/bombas/999999").contentType(MediaType.APPLICATION_JSON)
                .content(bombaJson(nomeUnico("X"), combustivelId)))
            .andExpect(status().isNotFound());
    }

    @Test
    void apagaBombaSemAbastecimentos() throws Exception {
        int id = criarBomba("Descartavel", criarCombustivel("Descartavel", "4.00"));

        mvc.perform(delete("/bombas/" + id))
            .andExpect(status().isNoContent());

        mvc.perform(get("/bombas/" + id))
            .andExpect(status().isNotFound());
    }
}
