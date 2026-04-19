package com.api.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ICT304 - TP : Banking API")
                        .version("1.0.0")
                        .description("API de Gestion Bancaire — ICT304 : Software Testing and Quality Assurance")
                        .contact(new Contact()
                                .name("DJINE SINTO PAFING (23U2292)")
                                .email("dsintopafing@gmail.com")))
                .tags(List.of(
                        new Tag().name("Admin").description("Gestion des comptes bancaires"),
                        new Tag().name("Client").description("Opérations bancaires client")
                ));
    }
}
