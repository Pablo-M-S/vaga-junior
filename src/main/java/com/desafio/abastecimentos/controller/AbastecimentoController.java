package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.dto.AbastecimentoRequest;
import com.desafio.abastecimentos.dto.AbastecimentoResponse;
import com.desafio.abastecimentos.service.AbastecimentoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Rotas REST de abastecimentos (/abastecimentos).
 * O controller só recebe a requisição, aciona o service e converte o resultado
 * para o DTO de resposta; as regras (preço praticado, valor total) ficam no service.
 */
@RestController
@RequestMapping("/abastecimentos")
public class AbastecimentoController {

    private final AbastecimentoService service;

    public AbastecimentoController(AbastecimentoService service) {
        this.service = service;
    }

    /**
     * GET /abastecimentos: lista paginada.
     * Filtros opcionais: bombaId, de e ate (yyyy-MM-dd). Paginação: page, size e sort.
     * Sem parâmetros, devolve a primeira página (20 itens), do mais recente para o mais antigo.
     */
    @GetMapping
    public Page<AbastecimentoResponse> listar(
            @RequestParam(required = false) Long bombaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @PageableDefault(size = 20, sort = "data", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(bombaId, de, ate, pageable).map(AbastecimentoResponse::de);
    }

    /** GET /abastecimentos/{id}: busca um (404 se não existir). */
    @GetMapping("/{id}")
    public AbastecimentoResponse buscar(@PathVariable Long id) {
        return AbastecimentoResponse.de(service.buscar(id));
    }

    /** POST /abastecimentos: cria (201). Preço praticado e valor total voltam calculados. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbastecimentoResponse criar(@Valid @RequestBody AbastecimentoRequest dados) {
        return AbastecimentoResponse.de(service.criar(dados));
    }

    /** PUT /abastecimentos/{id}: altera bomba, data e litros; o total é recalculado. */
    @PutMapping("/{id}")
    public AbastecimentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AbastecimentoRequest dados) {
        return AbastecimentoResponse.de(service.atualizar(id, dados));
    }

    /** DELETE /abastecimentos/{id}: apaga (204). */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
