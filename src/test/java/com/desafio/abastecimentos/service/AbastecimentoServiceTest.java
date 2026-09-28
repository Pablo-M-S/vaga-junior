package com.desafio.abastecimentos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desafio.abastecimentos.dto.AbastecimentoRequest;
import com.desafio.abastecimentos.model.Abastecimento;
import com.desafio.abastecimentos.model.Bomba;
import com.desafio.abastecimentos.model.Combustivel;
import com.desafio.abastecimentos.repository.AbastecimentoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AbastecimentoServiceTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 9, 1, 10, 0);

    @Mock
    private AbastecimentoRepository repository;

    @Mock
    private BombaService bombaService;

    @InjectMocks
    private AbastecimentoService service;

    /** Bomba cujo combustível custa o preço informado. */
    private Bomba bomba(long id, String preco) {
        Combustivel c = new Combustivel();
        c.setId(id);
        c.setNome("Combustivel " + id);
        c.setPrecoPorLitro(new BigDecimal(preco));
        Bomba b = new Bomba();
        b.setId(id);
        b.setNome("Bomba " + id);
        b.setCombustivel(c);
        return b;
    }

    private Abastecimento abastecimento(Bomba bomba, String litros, String precoGuardado) {
        Abastecimento a = new Abastecimento();
        a.setId(7L);
        a.setBomba(bomba);
        a.setData(DATA);
        a.setLitros(new BigDecimal(litros));
        a.setPrecoPorLitro(precoGuardado == null ? null : new BigDecimal(precoGuardado));
        return a;
    }

    /** Compara valores ignorando a escala (5.0 e 5.00 são iguais). */
    private void assertValor(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real), "esperado " + esperado + " mas foi " + real);
    }

    @Test
    void criaGuardandoOPrecoECalculandoOTotal() {
        when(bombaService.buscar(1L)).thenReturn(bomba(1L, "5.890"));
        when(repository.save(any(Abastecimento.class))).thenAnswer(i -> i.getArgument(0));

        Abastecimento criado = service.criar(new AbastecimentoRequest(1L, DATA, new BigDecimal("10")));

        assertValor("5.89", criado.getPrecoPorLitro());
        assertValor("58.90", criado.getValorTotal());
    }

    @Test
    void arredondaOTotalParaDuasCasas() {
        when(bombaService.buscar(1L)).thenReturn(bomba(1L, "5.890"));
        when(repository.save(any(Abastecimento.class))).thenAnswer(i -> i.getArgument(0));

        // 3,333 x 5,89 = 19,63137
        Abastecimento criado = service.criar(new AbastecimentoRequest(1L, DATA, new BigDecimal("3.333")));

        assertValor("19.63", criado.getValorTotal());
    }

    @Test
    void criarComBombaInexistenteLanca404() {
        when(bombaService.buscar(9L))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Bomba não encontrada"));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.criar(new AbastecimentoRequest(9L, DATA, BigDecimal.TEN)));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarNaMesmaBombaMantemOPrecoPraticadoNaEpoca() {
        // o combustível subiu para 6,00, mas o abastecimento foi feito a 5,00
        Bomba bomba = bomba(1L, "6.000");
        when(repository.findById(7L)).thenReturn(Optional.of(abastecimento(bomba, "10", "5.000")));
        when(bombaService.buscar(1L)).thenReturn(bomba);
        when(repository.save(any(Abastecimento.class))).thenAnswer(i -> i.getArgument(0));

        Abastecimento atualizado = service.atualizar(7L, new AbastecimentoRequest(1L, DATA, new BigDecimal("20")));

        assertValor("5.00", atualizado.getPrecoPorLitro());
        assertValor("100.00", atualizado.getValorTotal());
    }

    @Test
    void atualizarTrocandoDeBombaUsaOPrecoDoNovoCombustivel() {
        Bomba nova = bomba(2L, "4.000");
        when(repository.findById(7L)).thenReturn(Optional.of(abastecimento(bomba(1L, "6.000"), "10", "5.000")));
        when(bombaService.buscar(2L)).thenReturn(nova);
        when(repository.save(any(Abastecimento.class))).thenAnswer(i -> i.getArgument(0));

        Abastecimento atualizado = service.atualizar(7L, new AbastecimentoRequest(2L, DATA, new BigDecimal("10")));

        assertSame(nova, atualizado.getBomba());
        assertValor("4.00", atualizado.getPrecoPorLitro());
        assertValor("40.00", atualizado.getValorTotal());
    }

    @Test
    void atualizarRegistroAntigoSemPrecoUsaOPrecoAtual() {
        Bomba bomba = bomba(1L, "6.000");
        when(repository.findById(7L)).thenReturn(Optional.of(abastecimento(bomba, "10", null)));
        when(bombaService.buscar(1L)).thenReturn(bomba);
        when(repository.save(any(Abastecimento.class))).thenAnswer(i -> i.getArgument(0));

        Abastecimento atualizado = service.atualizar(7L, new AbastecimentoRequest(1L, DATA, new BigDecimal("10")));

        assertValor("6.00", atualizado.getPrecoPorLitro());
        assertValor("60.00", atualizado.getValorTotal());
    }

    @Test
    void atualizarInexistenteLanca404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.atualizar(99L, new AbastecimentoRequest(1L, DATA, BigDecimal.TEN)));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void apagaAbastecimentoExistente() {
        Abastecimento existente = abastecimento(bomba(1L, "5.000"), "10", "5.000");
        when(repository.findById(7L)).thenReturn(Optional.of(existente));

        service.deletar(7L);

        verify(repository).delete(existente);
    }

    @Test
    void apagarInexistenteLanca404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> service.deletar(99L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }
}
