package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class ErrosApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void apagarCombustivelComBombaRetorna409() throws Exception {
        String resposta = mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Etanol\",\"precoPorLitro\":3.99}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        int combustivelId = JsonPath.read(resposta, "$.id");

        mvc.perform(post("/bombas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Bomba E\",\"combustivel\":{\"id\":" + combustivelId + "}}"))
            .andExpect(status().isCreated());

        mvc.perform(delete("/combustiveis/" + combustivelId))
            .andExpect(status().isConflict())
            .andExpect(status().reason("Não é possível apagar: o registro está vinculado a outros dados"));
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
                .content("{\"nome\":\"Gasolina\",\"precoPorLitro\":4.12345}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.precoPorLitro").exists());

        mvc.perform(post("/combustiveis").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Gasolina\",\"precoPorLitro\":99999999.99}"))
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
