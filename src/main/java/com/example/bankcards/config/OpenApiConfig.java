package com.example.bankcards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact myContact = new Contact();
        myContact.setName("Вурц Геннадий");
        myContact.setEmail("vgd2001@gmail.com");

        Info info = new Info()
                .title("API Системы для управления банковскими картами")
                .version("1.0")
                .contact(myContact);

        return new OpenAPI().info(info);
    }
}
