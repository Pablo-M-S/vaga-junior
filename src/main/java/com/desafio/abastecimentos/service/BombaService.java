package com.desafio.abastecimentos.service;

import com.desafio.abastecimentos.dto.BombaRequest;
import com.desafio.abastecimentos.model.Bomba;
import com.desafio.abastecimentos.repository.BombaRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Regras de negócio das bombas: nome único e combustível sempre existente. */
@Service
public class BombaService {

    /** Acesso ao banco para bombas. */
    private final BombaRepository repository;
    /** Usado para conferir se o combustível informado existe. */
    private final CombustivelService combustivelService;

    /** Recebe as dependências por construtor (injeção feita pelo Spring). */
    public BombaService(BombaRepository repository, CombustivelService combustivelService) {
        this.repository = repository;
        this.combustivelService = combustivelService;
    }

    /** Devolve todas as bombas cadastradas. */
    public List<Bomba> listar() {
        return repository.findAll();
    }

    /** Busca por id; responde 404 se não existir. */
    public Bomba buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Bomba não encontrada"));
    }

    /** Cria uma bomba. 409 se o nome já existir; 404 se o combustível não existir. */
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

    /** Altera nome e combustível. Manter o próprio nome é permitido. */
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

    /** Apaga a bomba. Responde 409 se ainda houver abastecimentos ligados a ela. */
    public void deletar(Long id) {
        try {
            repository.delete(buscar(id));
        } catch (DataIntegrityViolationException e) {
            // o banco recusou por causa da chave estrangeira dos abastecimentos
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível apagar: o registro está vinculado a outros dados");
        }
    }

    /** Monta o erro 409 usado quando o nome da bomba já existe. */
    private ResponseStatusException nomeDuplicado() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma bomba com esse nome");
    }
}
