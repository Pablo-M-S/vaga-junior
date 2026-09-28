package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** Combustível e bomba não podem repetir o nome (sem diferenciar maiúsculas e minúsculas). */
class NomesDuplicadosTest extends ApiTestBase {

    @Test
    void combustivelComNomeRepetidoRetorna409() throws Exception {
        String nome = nomeUnico("Gasolina");
        criar("/combustiveis", combustivelJson(nome, "5.89"));

        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(nome, "6.00")))
            .andExpect(status().isConflict());

        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(nome.toUpperCase(), "6.00")))
            .andExpect(status().isConflict());
    }

    @Test
    void atualizarCombustivelParaNomeDeOutroRetorna409() throws Exception {
        String nomeA = nomeUnico("A");
        criar("/combustiveis", combustivelJson(nomeA, "5.00"));
        int idB = criarCombustivel("B", "4.00");

        mvc.perform(put("/combustiveis/" + idB).contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(nomeA, "4.00")))
            .andExpect(status().isConflict());
    }

    @Test
    void atualizarCombustivelMantendoOProprioNomeFunciona() throws Exception {
        String nome = nomeUnico("Gasolina");
        int id = criar("/combustiveis", combustivelJson(nome, "5.00"));

        mvc.perform(put("/combustiveis/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(combustivelJson(nome, "5.50")))
            .andExpect(status().isOk());
    }

    @Test
    void bombaComNomeRepetidoRetorna409() throws Exception {
        int combustivelId = criarCombustivel("Gasolina", "5.89");
        String nome = nomeUnico("Bomba 1");
        criar("/bombas", bombaJson(nome, combustivelId));

        mvc.perform(post("/bombas").contentType(MediaType.APPLICATION_JSON)
                .content(bombaJson(nome.toLowerCase(), combustivelId)))
            .andExpect(status().isConflict());
    }

    @Test
    void atualizarBombaParaNomeDeOutraRetorna409() throws Exception {
        int combustivelId = criarCombustivel("Gasolina", "5.89");
        String nomeA = nomeUnico("A");
        criar("/bombas", bombaJson(nomeA, combustivelId));
        int idB = criarBomba("B", combustivelId);

        mvc.perform(put("/bombas/" + idB).contentType(MediaType.APPLICATION_JSON)
                .content(bombaJson(nomeA, combustivelId)))
            .andExpect(status().isConflict());
    }

    @Test
    void atualizarBombaMantendoOProprioNomeFunciona() throws Exception {
        int combustivelId = criarCombustivel("Gasolina", "5.89");
        String nome = nomeUnico("Bomba 1");
        int id = criar("/bombas", bombaJson(nome, combustivelId));

        mvc.perform(put("/bombas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(bombaJson(nome, combustivelId)))
            .andExpect(status().isOk());
    }
}
