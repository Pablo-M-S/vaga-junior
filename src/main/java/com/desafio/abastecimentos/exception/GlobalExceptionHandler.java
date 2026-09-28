package com.desafio.abastecimentos.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Padroniza as respostas de erro da API em JSON. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 400: campos inválidos, indicando qual campo falhou e por quê. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException ex) {
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

    private Map<String, Object> corpo(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        return corpo;
    }
}
