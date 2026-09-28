package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class FiltroAbastecimentoTest {

    @Autowired
    private MockMvc mvc;

    private int criar(String url, String json) throws Exception {
        String resposta = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    @Test
    void filtraPorBombaEPeriodo() throws Exception {
        int combustivelId = criar("/combustiveis", "{\"nome\":\"Diesel\",\"precoPorLitro\":6.10}");
        int bombaId = criar("/bombas", "{\"nome\":\"Bomba D\",\"combustivel\":{\"id\":" + combustivelId + "}}");

        criar("/abastecimentos", "{\"bomba\":{\"id\":" + bombaId + "},\"data\":\"2026-09-10T10:00:00\",\"litros\":10}");
        criar("/abastecimentos", "{\"bomba\":{\"id\":" + bombaId + "},\"data\":\"2026-09-20T10:00:00\",\"litros\":5}");

        mvc.perform(get("/abastecimentos")
                .param("bombaId", String.valueOf(bombaId))
                .param("de", "2026-09-15")
                .param("ate", "2026-09-30"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].litros").value(5.0));
    }

    @Test
    void paginaOsResultados() throws Exception {
        mvc.perform(get("/abastecimentos").param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));
    }
}
