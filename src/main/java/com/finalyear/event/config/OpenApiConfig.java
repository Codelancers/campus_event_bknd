package com.finalyear.event.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Campus Event Management API")
                        .version("1.0.0")
                        .description("API documentation for Campus Event Management System. " +
                                "Note: Most endpoints require JWT authentication. Use the /api/users/verify or /api/admins/verify endpoints to obtain a token.")
                        .contact(new Contact()
                                .name("Event Management Team")
                                .email("infocodelancer@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
