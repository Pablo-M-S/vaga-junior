package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.model.Abastecimento;
import com.desafio.abastecimentos.service.AbastecimentoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/abastecimentos")
public class AbastecimentoController {

    private final AbastecimentoService service;

    public AbastecimentoController(AbastecimentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Abastecimento> listar() { return service.listar(); }

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
