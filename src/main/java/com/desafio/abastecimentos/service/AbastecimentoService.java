package com.desafio.abastecimentos.service;

import com.desafio.abastecimentos.dto.AbastecimentoRequest;
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

/**
 * Regras de negócio dos abastecimentos: confere a bomba, guarda o preço praticado
 * na época e calcula o valor total. O cliente nunca informa preço nem total.
 */
@Service
public class AbastecimentoService {

    private final AbastecimentoRepository repository;
    private final BombaService bombaService;

    public AbastecimentoService(AbastecimentoRepository repository, BombaService bombaService) {
        this.repository = repository;
        this.bombaService = bombaService;
    }

    /**
     * Lista com filtros opcionais (bomba e período, inclusive) e paginação.
     * A consulta é montada na hora, só com os filtros que foram informados.
     */
    public Page<Abastecimento> listar(Long bombaId, LocalDate de, LocalDate ate, Pageable pageable) {
        // começa sem nenhuma condição e vai acrescentando as que vieram na requisição
        Specification<Abastecimento> spec = Specification.where(null);
        if (bombaId != null) {
            spec = spec.and((raiz, q, cb) -> cb.equal(raiz.get("bomba").get("id"), bombaId));
        }
        if (de != null) {
            // "de" inclui o dia inteiro: vale a partir da meia-noite
            LocalDateTime inicio = de.atStartOfDay();
            spec = spec.and((raiz, q, cb) ->
                cb.greaterThanOrEqualTo(raiz.<LocalDateTime>get("data"), inicio));
        }
        if (ate != null) {
            // "ate" também é inclusivo: pega tudo que for antes da meia-noite do dia seguinte
            LocalDateTime fim = ate.plusDays(1).atStartOfDay();
            spec = spec.and((raiz, q, cb) -> cb.lessThan(raiz.<LocalDateTime>get("data"), fim));
        }
        return repository.findAll(spec, pageable);
    }

    /** Busca por id; responde 404 se não existir. */
    public Abastecimento buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Abastecimento não encontrado"));
    }

    /** Guarda o preço do combustível no momento do abastecimento e calcula o valor total. */
    public Abastecimento criar(AbastecimentoRequest dados) {
        // buscar() responde 404 se a bomba não existir
        Bomba bomba = bombaService.buscar(dados.bombaId());
        Abastecimento abastecimento = new Abastecimento();
        abastecimento.setBomba(bomba);
        abastecimento.setData(dados.data());
        abastecimento.setLitros(dados.litros());
        // cópia do preço de agora: reajustes futuros não mudam este registro
        abastecimento.setPrecoPorLitro(bomba.getCombustivel().getPrecoPorLitro());
        abastecimento.setValorTotal(calcularTotal(dados.litros(), abastecimento.getPrecoPorLitro()));
        return repository.save(abastecimento);
    }

    /**
     * Mantém o preço praticado na época. O preço só é atualizado se a bomba mudar
     * (novo combustível) ou se o registro for antigo e não tiver preço guardado.
     */
    public Abastecimento atualizar(Long id, AbastecimentoRequest dados) {
        Abastecimento existente = buscar(id);
        Bomba bomba = bombaService.buscar(dados.bombaId());

        boolean mesmaBomba = existente.getBomba() != null
            && existente.getBomba().getId().equals(bomba.getId());
        if (!mesmaBomba || existente.getPrecoPorLitro() == null) {
            existente.setPrecoPorLitro(bomba.getCombustivel().getPrecoPorLitro());
        }
        existente.setBomba(bomba);
        existente.setData(dados.data());
        existente.setLitros(dados.litros());
        // o total é sempre recalculado, mas com o preço guardado (não o atual)
        existente.setValorTotal(calcularTotal(dados.litros(), existente.getPrecoPorLitro()));
        return repository.save(existente);
    }

    /** Apaga o abastecimento; responde 404 se não existir. */
    public void deletar(Long id) {
        repository.delete(buscar(id));
    }

    /** litros x preço por litro, arredondado a 2 casas. */
    private BigDecimal calcularTotal(BigDecimal litros, BigDecimal preco) {
        return litros.multiply(preco).setScale(2, RoundingMode.HALF_UP);
    }
}
