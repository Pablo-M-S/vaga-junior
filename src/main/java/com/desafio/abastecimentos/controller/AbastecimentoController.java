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

@RestController
@RequestMapping("/abastecimentos")
public class AbastecimentoController {

    private final AbastecimentoService service;

    public AbastecimentoController(AbastecimentoService service) {
        this.service = service;
    }

    /** Filtros opcionais: bombaId, de e ate (yyyy-MM-dd). Paginação: page e size. */
    @GetMapping
    public Page<AbastecimentoResponse> listar(
            @RequestParam(required = false) Long bombaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @PageableDefault(size = 20, sort = "data", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(bombaId, de, ate, pageable).map(AbastecimentoResponse::de);
    }

    @GetMapping("/{id}")
    public AbastecimentoResponse buscar(@PathVariable Long id) {
        return AbastecimentoResponse.de(service.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbastecimentoResponse criar(@Valid @RequestBody AbastecimentoRequest dados) {
        return AbastecimentoResponse.de(service.criar(dados));
    }

    @PutMapping("/{id}")
    public AbastecimentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AbastecimentoRequest dados) {
        return AbastecimentoResponse.de(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
