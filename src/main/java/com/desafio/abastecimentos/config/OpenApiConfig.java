package com.desafio.abastecimentos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadados exibidos na documentação Swagger (/swagger-ui/index.html).
 * As rotas e os corpos são lidos dos controllers e dos DTOs pelo springdoc;
 * aqui só definimos o título, a versão e a descrição.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
            .title("API de Abastecimentos")
            .version("1.0")
            .description("Cadastro e consulta de combustíveis, bombas e abastecimentos de um posto."));
    }
}
