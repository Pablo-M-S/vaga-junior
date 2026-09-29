package com.desafio.abastecimentos.service;

import com.desafio.abastecimentos.dto.CombustivelRequest;
import com.desafio.abastecimentos.model.Combustivel;
import com.desafio.abastecimentos.repository.CombustivelRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Regras de negócio dos combustíveis: nome único, 404 e 409 com mensagens claras. */
@Service
public class CombustivelService {

    private final CombustivelRepository repository;

    public CombustivelService(CombustivelRepository repository) {
        this.repository = repository;
    }

    public List<Combustivel> listar() {
        return repository.findAll();
    }

    public Combustivel buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Combustível não encontrado"));
    }

    /** Cria um combustível novo. Responde 409 se já existir outro com o mesmo nome. */
    public Combustivel criar(CombustivelRequest dados) {
        String nome = dados.nome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw nomeDuplicado();
        }
        Combustivel combustivel = new Combustivel();
        combustivel.setNome(nome);
        combustivel.setPrecoPorLitro(dados.precoPorLitro());
        return repository.save(combustivel);
    }

    /**
     * Altera nome e preço. Manter o próprio nome é permitido; usar o nome de
     * outro combustível responde 409. Abastecimentos antigos não mudam, pois
     * guardam o preço praticado na época.
     */
    public Combustivel atualizar(Long id, CombustivelRequest dados) {
        Combustivel existente = buscar(id);
        String nome = dados.nome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw nomeDuplicado();
        }
        existente.setNome(nome);
        existente.setPrecoPorLitro(dados.precoPorLitro());
        return repository.save(existente);
    }

    /** Apaga o combustível. Responde 409 se alguma bomba ainda estiver ligada a ele. */
    public void deletar(Long id) {
        try {
            repository.delete(buscar(id));
        } catch (DataIntegrityViolationException e) {
            // o banco recusou por causa da chave estrangeira das bombas
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível apagar: o registro está vinculado a outros dados");
        }
    }

    private ResponseStatusException nomeDuplicado() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um combustível com esse nome");
    }
}
