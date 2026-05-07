package com.api.banking.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "X-Role";

    @Bean
    public OpenAPI bankingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ICT304 - TP : Banking API")
                        .version("1.0.0")
                        .description("""
                                API de Gestion Bancaire — ICT304 : Software Testing and Quality Assurance

                                **Authentification :** ajoutez l'en-tête `X-Role` avec la valeur `ADMIN` ou `CLIENT`.
                                """)
                        .contact(new Contact()
                                .name("DJINE SINTO PAFING (23U2292)")
                                .email("dsintopafing@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(SCHEME_NAME)
                                .description("Entrez le rôle : **ADMIN** ou **CLIENT**")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .tags(List.of(
                        new Tag().name("Admin - Banques").description("Gestion du réseau de banques partenaires"),
                        new Tag().name("Admin - Comptes").description("Gestion des comptes bancaires"),
                        new Tag().name("Client").description("Opérations bancaires client")
                ));
    }
}
