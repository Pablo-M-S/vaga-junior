package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.model.Combustivel;
import com.desafio.abastecimentos.service.CombustivelService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/combustiveis")
public class CombustivelController {

    private final CombustivelService service;

    public CombustivelController(CombustivelService service) {
        this.service = service;
    }

    @GetMapping
    public List<Combustivel> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Combustivel buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Combustivel criar(@Valid @RequestBody Combustivel c) { return service.criar(c); }

    @PutMapping("/{id}")
    public Combustivel atualizar(@PathVariable Long id, @Valid @RequestBody Combustivel c) {
        return service.atualizar(id, c);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
