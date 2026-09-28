package com.desafio.abastecimentos.service;

import com.desafio.abastecimentos.model.Abastecimento;
import com.desafio.abastecimentos.model.Bomba;
import com.desafio.abastecimentos.repository.AbastecimentoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AbastecimentoService {

    private final AbastecimentoRepository repository;
    private final BombaService bombaService;

    public AbastecimentoService(AbastecimentoRepository repository, BombaService bombaService) {
        this.repository = repository;
        this.bombaService = bombaService;
    }

    public List<Abastecimento> listar() {
        return repository.findAll();
    }

    public Abastecimento buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Abastecimento não encontrado"));
    }

    public Abastecimento criar(Abastecimento abastecimento) {
        abastecimento.setId(null);
        preencher(abastecimento);
        return repository.save(abastecimento);
    }

    public Abastecimento atualizar(Long id, Abastecimento dados) {
        Abastecimento existente = buscar(id);
        existente.setBomba(dados.getBomba());
        existente.setData(dados.getData());
        existente.setLitros(dados.getLitros());
        preencher(existente);
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.delete(buscar(id));
    }

    /** Valida a bomba informada e calcula o valor total (litros x preço por litro). */
    private void preencher(Abastecimento a) {
        Long bombaId = a.getBomba().getId();
        if (bombaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id da bomba");
        }
        Bomba bomba = bombaService.buscar(bombaId);
        a.setBomba(bomba);
        BigDecimal preco = bomba.getCombustivel().getPrecoPorLitro();
        a.setValorTotal(a.getLitros().multiply(preco).setScale(2, RoundingMode.HALF_UP));
    }
}
