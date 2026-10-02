package com.desafio.abastecimentos.exception;

import jakarta.servlet.ServletException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Padroniza as respostas de erro da API em JSON, num só lugar: timestamp, status, erro e mensagem.
 * Os 400 de validação acrescentam o mapa "campos". Os 404 e 409 são lançados pelos
 * services (ResponseStatusException) e saem no mesmo formato, assim como rota inexistente
 * (404), método não permitido (405) e erros inesperados (500).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404, 409 e demais erros de negócio lançados pelos services. */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> erroDeNegocio(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String mensagem = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return ResponseEntity.status(status).body(corpo(status, mensagem));
    }

    /** 400: campos inválidos, indicando qual campo falhou e por quê. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException ex) {
        // mapa campo -> mensagem; putIfAbsent guarda só a primeira mensagem de cada campo
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        Map<String, Object> corpo = corpo(HttpStatus.BAD_REQUEST, "Dados inválidos");
        corpo.put("campos", campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    /** 400: JSON malformado ou valor em formato errado (ex.: data inválida). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(corpo(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formatado"));
    }

    /** 400: violação de restrição do banco (ex.: valor maior que a coluna aceita). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> violacaoDeRestricao(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo(HttpStatus.BAD_REQUEST,
            "Dados inválidos: valor fora do limite permitido"));
    }

    /** 400: parâmetro com tipo errado (ex.: data inválida em ?de= ou id não numérico). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> tipoInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(corpo(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + ex.getName() + "'"));
    }

    /** 400: ordenação por campo que não existe (ex.: ?sort=foo). */
    @ExceptionHandler({PropertyReferenceException.class,
        InvalidDataAccessApiUsageException.class})
    public ResponseEntity<Map<String, Object>> consultaInvalida(Exception ex) {
        // registra a causa: esse handler é amplo e não deve esconder um bug real como se fosse erro do cliente
        log.warn("Consulta rejeitada com 400: {}", ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(corpo(HttpStatus.BAD_REQUEST, "Parâmetros de consulta inválidos (confira o campo de ordenação)"));
    }

    /** Rota inexistente (404), método não permitido (405), tipo de conteúdo não aceito (415) etc. */
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Map<String, Object>> erroDoServlet(ServletException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders cabecalhos = HttpHeaders.EMPTY;
        if (ex instanceof ErrorResponse resposta) {
            status = HttpStatus.valueOf(resposta.getStatusCode().value());
            cabecalhos = resposta.getHeaders(); // ex.: o cabeçalho Allow do 405
        }
        if (status.is5xxServerError()) {
            log.error("Erro inesperado no servlet", ex);
        }
        String mensagem = switch (status) {
            case NOT_FOUND -> "Rota não encontrada";
            case METHOD_NOT_ALLOWED -> "Método não permitido para esta rota";
            case UNSUPPORTED_MEDIA_TYPE -> "Tipo de conteúdo não suportado";
            case INTERNAL_SERVER_ERROR -> "Erro interno do servidor";
            default -> status.getReasonPhrase();
        };
        return ResponseEntity.status(status).headers(cabecalhos).body(corpo(status, mensagem));
    }

    /** 500: qualquer erro não previsto sai no mesmo formato JSON, sem vazar detalhes internos. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> erroInesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(corpo(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor"));
    }

    private Map<String, Object> corpo(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        return corpo;
    }
}
