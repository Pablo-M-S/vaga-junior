package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.model.Bomba;
import com.desafio.abastecimentos.service.BombaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bombas")
public class BombaController {

    private final BombaService service;

    public BombaController(BombaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Bomba> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Bomba buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Bomba criar(@Valid @RequestBody Bomba b) { return service.criar(b); }

    @PutMapping("/{id}")
    public Bomba atualizar(@PathVariable Long id, @Valid @RequestBody Bomba b) {
        return service.atualizar(id, b);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
