package com.desafio.abastecimentos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desafio.abastecimentos.dto.BombaRequest;
import com.desafio.abastecimentos.model.Bomba;
import com.desafio.abastecimentos.model.Combustivel;
import com.desafio.abastecimentos.repository.BombaRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BombaServiceTest {

    @Mock
    private BombaRepository repository;

    @Mock
    private CombustivelService combustivelService;

    @InjectMocks
    private BombaService service;

    private Combustivel combustivel(long id) {
        Combustivel c = new Combustivel();
        c.setId(id);
        c.setNome("Combustivel " + id);
        return c;
    }

    private Bomba bomba(long id, String nome, Combustivel combustivel) {
        Bomba b = new Bomba();
        b.setId(id);
        b.setNome(nome);
        b.setCombustivel(combustivel);
        return b;
    }

    @Test
    void criaBombaComOCombustivelInformado() {
        Combustivel gasolina = combustivel(1L);
        when(combustivelService.buscar(1L)).thenReturn(gasolina);
        when(repository.save(any(Bomba.class))).thenAnswer(i -> i.getArgument(0));

        Bomba criada = service.criar(new BombaRequest("  Bomba 1 ", 1L));

        assertEquals("Bomba 1", criada.getNome());
        assertSame(gasolina, criada.getCombustivel());
    }

    @Test
    void criarComCombustivelInexistenteLanca404() {
        when(combustivelService.buscar(9L))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Combustível não encontrado"));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.criar(new BombaRequest("Bomba 1", 9L)));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void criarComNomeDuplicadoLanca409() {
        when(repository.existsByNomeIgnoreCase("Bomba 1")).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.criar(new BombaRequest("Bomba 1", 1L)));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void buscarInexistenteLanca404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> service.buscar(99L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void atualizaNomeECombustivel() {
        Combustivel etanol = combustivel(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(bomba(1L, "Bomba 1", combustivel(1L))));
        when(combustivelService.buscar(2L)).thenReturn(etanol);
        when(repository.save(any(Bomba.class))).thenAnswer(i -> i.getArgument(0));

        Bomba atualizada = service.atualizar(1L, new BombaRequest("Bomba Etanol", 2L));

        assertEquals("Bomba Etanol", atualizada.getNome());
        assertSame(etanol, atualizada.getCombustivel());
    }

    @Test
    void atualizarParaNomeDeOutraBombaLanca409() {
        when(repository.findById(1L)).thenReturn(Optional.of(bomba(1L, "Bomba 1", combustivel(1L))));
        when(repository.existsByNomeIgnoreCaseAndIdNot("Bomba 2", 1L)).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.atualizar(1L, new BombaRequest("Bomba 2", 1L)));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void apagarBombaComAbastecimentosLanca409() {
        Bomba existente = bomba(1L, "Bomba 1", combustivel(1L));
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        doThrow(new DataIntegrityViolationException("fk")).when(repository).delete(existente);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> service.deletar(1L));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }
}
