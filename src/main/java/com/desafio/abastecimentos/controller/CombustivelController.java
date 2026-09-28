package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.dto.CombustivelRequest;
import com.desafio.abastecimentos.dto.CombustivelResponse;
import com.desafio.abastecimentos.service.CombustivelService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Rotas REST de combustíveis (/combustiveis).
 * O controller só recebe a requisição, aciona o service e converte o resultado
 * para o DTO de resposta; as regras ficam no service.
 */
@RestController
@RequestMapping("/combustiveis")
public class CombustivelController {

    private final CombustivelService service;

    public CombustivelController(CombustivelService service) {
        this.service = service;
    }

    /** GET /combustiveis: lista todos. */
    @GetMapping
    public List<CombustivelResponse> listar() {
        return service.listar().stream().map(CombustivelResponse::de).toList();
    }

    /** GET /combustiveis/{id}: busca um (404 se não existir). */
    @GetMapping("/{id}")
    public CombustivelResponse buscar(@PathVariable Long id) {
        return CombustivelResponse.de(service.buscar(id));
    }

    /** POST /combustiveis: cria (201). O @Valid aplica as regras do CombustivelRequest. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CombustivelResponse criar(@Valid @RequestBody CombustivelRequest dados) {
        return CombustivelResponse.de(service.criar(dados));
    }

    /** PUT /combustiveis/{id}: altera nome e preço. */
    @PutMapping("/{id}")
    public CombustivelResponse atualizar(@PathVariable Long id, @Valid @RequestBody CombustivelRequest dados) {
        return CombustivelResponse.de(service.atualizar(id, dados));
    }

    /** DELETE /combustiveis/{id}: apaga (204). 409 se houver bombas ligadas a ele. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
