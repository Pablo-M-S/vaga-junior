package com.desafio.abastecimentos.service;

import com.desafio.abastecimentos.model.Combustivel;
import com.desafio.abastecimentos.repository.CombustivelRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Combustivel criar(Combustivel combustivel) {
        combustivel.setId(null);
        return repository.save(combustivel);
    }

    public Combustivel atualizar(Long id, Combustivel dados) {
        Combustivel existente = buscar(id);
        existente.setNome(dados.getNome());
        existente.setPrecoPorLitro(dados.getPrecoPorLitro());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.delete(buscar(id));
    }
}
