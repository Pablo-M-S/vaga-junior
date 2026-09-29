package com.desafio.abastecimentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base dos testes de API: MockMvc e atalhos para criar dados.
 * Os testes compartilham o mesmo banco em memória, então cada um usa nomes únicos.
 */
@SpringBootTest
@AutoConfigureMockMvc
abstract class ApiTestBase {

    @Autowired
    protected MockMvc mvc;

    protected int criar(String url, String json) throws Exception {
        String resposta = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    /** Gera um nome único (sufixo aleatório) para os testes não colidirem entre si. */
    protected String nomeUnico(String base) {
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    protected String combustivelJson(String nome, String preco) {
        return "{\"nome\":\"" + nome + "\",\"precoPorLitro\":" + preco + "}";
    }

    protected String bombaJson(String nome, int combustivelId) {
        return "{\"nome\":\"" + nome + "\",\"combustivelId\":" + combustivelId + "}";
    }

    protected String abastecimentoJson(int bombaId, String data, String litros) {
        return "{\"bombaId\":" + bombaId + ",\"data\":\"" + data + "\",\"litros\":" + litros + "}";
    }

    protected int criarCombustivel(String nomeBase, String preco) throws Exception {
        return criar("/combustiveis", combustivelJson(nomeUnico(nomeBase), preco));
    }

    protected int criarBomba(String nomeBase, int combustivelId) throws Exception {
        return criar("/bombas", bombaJson(nomeUnico(nomeBase), combustivelId));
    }

    protected int criarAbastecimento(int bombaId, String data, String litros) throws Exception {
        return criar("/abastecimentos", abastecimentoJson(bombaId, data, litros));
    }
}
