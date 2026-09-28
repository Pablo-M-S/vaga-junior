package com.desafio.abastecimentos.service;

import com.desafio.abastecimentos.dto.BombaRequest;
import com.desafio.abastecimentos.model.Bomba;
import com.desafio.abastecimentos.repository.BombaRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BombaService {

    private final BombaRepository repository;
    private final CombustivelService combustivelService;

    public BombaService(BombaRepository repository, CombustivelService combustivelService) {
        this.repository = repository;
        this.combustivelService = combustivelService;
    }

    public List<Bomba> listar() {
        return repository.findAll();
    }

    public Bomba buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Bomba não encontrada"));
    }

    public Bomba criar(BombaRequest dados) {
        String nome = dados.nome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw nomeDuplicado();
        }
        Bomba bomba = new Bomba();
        bomba.setNome(nome);
        // buscar() responde 404 se o combustível não existir
        bomba.setCombustivel(combustivelService.buscar(dados.combustivelId()));
        return repository.save(bomba);
    }

    public Bomba atualizar(Long id, BombaRequest dados) {
        Bomba existente = buscar(id);
        String nome = dados.nome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw nomeDuplicado();
        }
        existente.setNome(nome);
        existente.setCombustivel(combustivelService.buscar(dados.combustivelId()));
        return repository.save(existente);
    }

    public void deletar(Long id) {
        try {
            repository.delete(buscar(id));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível apagar: o registro está vinculado a outros dados");
        }
    }

    private ResponseStatusException nomeDuplicado() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma bomba com esse nome");
    }
}
