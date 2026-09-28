package com.desafio.abastecimentos.controller;

import com.desafio.abastecimentos.dto.BombaRequest;
import com.desafio.abastecimentos.dto.BombaResponse;
import com.desafio.abastecimentos.service.BombaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Rotas REST de bombas (/bombas).
 * O controller só recebe a requisição, aciona o service e converte o resultado
 * para o DTO de resposta; as regras ficam no service.
 */
@RestController
@RequestMapping("/bombas")
public class BombaController {

    private final BombaService service;

    public BombaController(BombaService service) {
        this.service = service;
    }

    /** GET /bombas: lista todas, cada uma com o seu combustível. */
    @GetMapping
    public List<BombaResponse> listar() {
        return service.listar().stream().map(BombaResponse::de).toList();
    }

    /** GET /bombas/{id}: busca uma (404 se não existir). */
    @GetMapping("/{id}")
    public BombaResponse buscar(@PathVariable Long id) {
        return BombaResponse.de(service.buscar(id));
    }

    /** POST /bombas: cria (201). O corpo informa o combustível pelo combustivelId. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BombaResponse criar(@Valid @RequestBody BombaRequest dados) {
        return BombaResponse.de(service.criar(dados));
    }

    /** PUT /bombas/{id}: altera nome e combustível. */
    @PutMapping("/{id}")
    public BombaResponse atualizar(@PathVariable Long id, @Valid @RequestBody BombaRequest dados) {
        return BombaResponse.de(service.atualizar(id, dados));
    }

    /** DELETE /bombas/{id}: apaga (204). 409 se houver abastecimentos ligados a ela. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { service.deletar(id); }
}
