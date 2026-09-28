package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.dto.CombustivelRequest;
import com.desafio.abastecimentos.dto.CombustivelResponse;
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
    public List<CombustivelResponse> listar() {
        return service.listar().stream().map(CombustivelResponse::de).toList();
    }

    @GetMapping("/{id}")
    public CombustivelResponse buscar(@PathVariable Long id) {
        return CombustivelResponse.de(service.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CombustivelResponse criar(@Valid @RequestBody CombustivelRequest dados) {
        return CombustivelResponse.de(service.criar(dados));
    }

    @PutMapping("/{id}")
    public CombustivelResponse atualizar(@PathVariable Long id, @Valid @RequestBody CombustivelRequest dados) {
        return CombustivelResponse.de(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
