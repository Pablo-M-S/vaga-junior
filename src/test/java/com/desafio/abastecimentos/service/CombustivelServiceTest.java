package com.desafio.abastecimentos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desafio.abastecimentos.dto.CombustivelRequest;
import com.desafio.abastecimentos.model.Combustivel;
import com.desafio.abastecimentos.repository.CombustivelRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Testa as regras do service sem subir o Spring (repositório simulado). */
@ExtendWith(MockitoExtension.class)
class CombustivelServiceTest {

    @Mock
    private CombustivelRepository repository;

    @InjectMocks
    private CombustivelService service;

    private Combustivel combustivel(long id, String nome, String preco) {
        Combustivel c = new Combustivel();
        c.setId(id);
        c.setNome(nome);
        c.setPrecoPorLitro(new BigDecimal(preco));
        return c;
    }

    @Test
    void criaCombustivelRemovendoEspacosDoNome() {
        when(repository.save(any(Combustivel.class))).thenAnswer(i -> i.getArgument(0));

        Combustivel criado = service.criar(new CombustivelRequest("  Gasolina  ", new BigDecimal("5.89")));

        assertEquals("Gasolina", criado.getNome());
        assertEquals(new BigDecimal("5.89"), criado.getPrecoPorLitro());
    }

    @Test
    void criarComNomeDuplicadoLanca409() {
        when(repository.existsByNomeIgnoreCase("Gasolina")).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.criar(new CombustivelRequest("Gasolina", new BigDecimal("5.89"))));

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
    void atualizaNomeEPreco() {
        when(repository.findById(1L)).thenReturn(Optional.of(combustivel(1L, "Gasolina", "5.00")));
        when(repository.save(any(Combustivel.class))).thenAnswer(i -> i.getArgument(0));

        Combustivel atualizado = service.atualizar(1L, new CombustivelRequest("Gasolina Aditivada", new BigDecimal("6.50")));

        assertEquals("Gasolina Aditivada", atualizado.getNome());
        assertEquals(new BigDecimal("6.50"), atualizado.getPrecoPorLitro());
    }

    @Test
    void atualizarParaNomeDeOutroCombustivelLanca409() {
        when(repository.findById(1L)).thenReturn(Optional.of(combustivel(1L, "Gasolina", "5.00")));
        when(repository.existsByNomeIgnoreCaseAndIdNot("Etanol", 1L)).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.atualizar(1L, new CombustivelRequest("Etanol", new BigDecimal("4.00"))));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void apagarCombustivelVinculadoLanca409() {
        Combustivel existente = combustivel(1L, "Gasolina", "5.00");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        doThrow(new DataIntegrityViolationException("fk")).when(repository).delete(existente);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> service.deletar(1L));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void apagaCombustivelExistente() {
        Combustivel existente = combustivel(1L, "Gasolina", "5.00");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.deletar(1L);

        verify(repository).delete(existente);
    }
}
