package com.desafio.abastecimentos.service;

import com.desafio.abastecimentos.model.Abastecimento;
import com.desafio.abastecimentos.model.Bomba;
import com.desafio.abastecimentos.repository.AbastecimentoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    /** Lista com filtros opcionais (bomba e período, inclusive) e paginação. */
    public Page<Abastecimento> listar(Long bombaId, LocalDate de, LocalDate ate, Pageable pageable) {
        Specification<Abastecimento> spec = Specification.where(null);
        if (bombaId != null) {
            spec = spec.and((raiz, q, cb) -> cb.equal(raiz.get("bomba").get("id"), bombaId));
        }
        if (de != null) {
            LocalDateTime inicio = de.atStartOfDay();
            spec = spec.and((raiz, q, cb) ->
                cb.greaterThanOrEqualTo(raiz.<LocalDateTime>get("data"), inicio));
        }
        if (ate != null) {
            LocalDateTime fim = ate.plusDays(1).atStartOfDay();
            spec = spec.and((raiz, q, cb) -> cb.lessThan(raiz.<LocalDateTime>get("data"), fim));
        }
        return repository.findAll(spec, pageable);
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
