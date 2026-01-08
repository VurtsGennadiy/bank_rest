package com.example.bankcards.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "JWT authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact myContact = new Contact();
        myContact.setName("Вурц Геннадий");
        myContact.setEmail("vgd2001@gmail.com");

        Info info = new Info()
                .title("API Системы для управления банковскими картами")
                .version("2.0")
                .contact(myContact);

        return new OpenAPI().info(info);
    }
}
