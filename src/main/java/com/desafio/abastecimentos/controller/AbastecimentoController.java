package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.model.Abastecimento;
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
    public Page<Abastecimento> listar(
            @RequestParam(required = false) Long bombaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @PageableDefault(size = 20, sort = "data", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(bombaId, de, ate, pageable);
    }

    @GetMapping("/{id}")
    public Abastecimento buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Abastecimento criar(@Valid @RequestBody Abastecimento a) { return service.criar(a); }

    @PutMapping("/{id}")
    public Abastecimento atualizar(@PathVariable Long id, @Valid @RequestBody Abastecimento a) {
        return service.atualizar(id, a);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
