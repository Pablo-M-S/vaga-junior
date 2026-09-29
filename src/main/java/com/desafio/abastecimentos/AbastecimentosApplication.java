package com.desafio.abastecimentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API de abastecimentos.
 * O @SpringBootApplication liga a configuração automática e varre os pacotes
 * abaixo deste (controller, service, repository etc.) para registrar os componentes.
 */
@SpringBootApplication
public class AbastecimentosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbastecimentosApplication.class, args);
    }
}
