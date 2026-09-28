package com.desafio.abastecimentos.service;

import com.desafio.abastecimentos.model.Bomba;
import com.desafio.abastecimentos.model.Combustivel;
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

    public Bomba criar(Bomba bomba) {
        bomba.setId(null);
        bomba.setCombustivel(resolverCombustivel(bomba));
        return repository.save(bomba);
    }

    public Bomba atualizar(Long id, Bomba dados) {
        Bomba existente = buscar(id);
        existente.setNome(dados.getNome());
        existente.setCombustivel(resolverCombustivel(dados));
        return repository.save(existente);
    }

    public void deletar(Long id) {
        try {
            repository.delete(buscar(id));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível apagar: o registro está vinculado a outros dados");
        }
    }

    /** Garante que o combustível informado existe (retorna 404 se não existir). */
    private Combustivel resolverCombustivel(Bomba bomba) {
        Long combustivelId = bomba.getCombustivel().getId();
        if (combustivelId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id do combustível");
        }
        return combustivelService.buscar(combustivelId);
    }
}
