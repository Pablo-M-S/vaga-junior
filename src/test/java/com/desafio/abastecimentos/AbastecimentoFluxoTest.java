package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AbastecimentoFluxoTest {

    @Autowired
    private MockMvc mvc;

    /** Cria um recurso via POST e devolve o id gerado. */
    private int criar(String url, String json) throws Exception {
        String resposta = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    @Test
    void calculaValorTotalDoAbastecimento() throws Exception {
        int combustivelId = criar("/combustiveis", "{\"nome\":\"Gasolina\",\"precoPorLitro\":5.89}");
        int bombaId = criar("/bombas", "{\"nome\":\"Bomba 1\",\"combustivel\":{\"id\":" + combustivelId + "}}");

        mvc.perform(post("/abastecimentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"bomba\":{\"id\":" + bombaId + "},\"data\":\"2026-09-28T17:40:00\",\"litros\":10}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.valorTotal").value(58.90));
    }

    @Test
    void bombaInexistenteRetorna404() throws Exception {
        mvc.perform(post("/abastecimentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"bomba\":{\"id\":9999},\"data\":\"2026-09-28T17:40:00\",\"litros\":10}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void combustivelInvalidoRetorna400() throws Exception {
        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"precoPorLitro\":-1}"))
            .andExpect(status().isBadRequest());
    }
}
