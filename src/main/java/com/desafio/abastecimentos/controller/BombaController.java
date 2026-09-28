package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.dto.BombaRequest;
import com.desafio.abastecimentos.dto.BombaResponse;
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
    public List<BombaResponse> listar() {
        return service.listar().stream().map(BombaResponse::de).toList();
    }

    @GetMapping("/{id}")
    public BombaResponse buscar(@PathVariable Long id) {
        return BombaResponse.de(service.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BombaResponse criar(@Valid @RequestBody BombaRequest dados) {
        return BombaResponse.de(service.criar(dados));
    }

    @PutMapping("/{id}")
    public BombaResponse atualizar(@PathVariable Long id, @Valid @RequestBody BombaRequest dados) {
        return BombaResponse.de(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
