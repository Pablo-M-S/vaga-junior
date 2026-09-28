package com.desafio.abastecimentos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * Serializa as páginas num formato JSON estável: { "content": [...], "page": {...} }.
 * Sem isso, o Spring devolveria a estrutura interna do PageImpl, que pode mudar
 * entre versões e gera um aviso no log.
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class PaginacaoConfig {
}
