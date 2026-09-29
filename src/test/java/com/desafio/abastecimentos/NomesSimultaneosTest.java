package com.desafio.abastecimentos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** Requisições simultâneas com o mesmo nome: uma cria (201) e as outras recebem 409. */
class NomesSimultaneosTest extends ApiTestBase {

    private static final int REQUISICOES = 4;

    @Test
    void combustivelSimultaneoComMesmoNomeCriaSoUm() throws Exception {
        String corpo = combustivelJson(nomeUnico("Gasolina"), "5.89");

        List<Integer> respostas = emParalelo(() -> mvc.perform(post("/combustiveis")
                .contentType(MediaType.APPLICATION_JSON).content(corpo))
            .andReturn().getResponse().getStatus());

        assertEquals(1, respostas.stream().filter(s -> s == 201).count());
        assertEquals(REQUISICOES - 1, respostas.stream().filter(s -> s == 409).count());
    }

    @Test
    void bombaSimultaneaComMesmoNomeCriaSoUma() throws Exception {
        int combustivelId = criarCombustivel("Etanol", "3.99");
        String corpo = bombaJson(nomeUnico("Bomba"), combustivelId);

        List<Integer> respostas = emParalelo(() -> mvc.perform(post("/bombas")
                .contentType(MediaType.APPLICATION_JSON).content(corpo))
            .andReturn().getResponse().getStatus());

        assertEquals(1, respostas.stream().filter(s -> s == 201).count());
        assertEquals(REQUISICOES - 1, respostas.stream().filter(s -> s == 409).count());
    }

    /** Dispara a mesma requisição em várias threads, todas liberadas ao mesmo tempo. */
    private List<Integer> emParalelo(Callable<Integer> requisicao) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(REQUISICOES);
        CountDownLatch largada = new CountDownLatch(1);
        try {
            List<Future<Integer>> futuros = new ArrayList<>();
            for (int i = 0; i < REQUISICOES; i++) {
                futuros.add(pool.submit(() -> {
                    largada.await();
                    return requisicao.call();
                }));
            }
            largada.countDown();
            List<Integer> status = new ArrayList<>();
            for (Future<Integer> futuro : futuros) {
                status.add(futuro.get());
            }
            return status;
        } finally {
            pool.shutdownNow();
        }
    }
}
