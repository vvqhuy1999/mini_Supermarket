package com.example.mini_supermarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini Supermarket API")
                        .version("1.0.0")
                        .description("API documentation for Mini Supermarket Management System")
                        .contact(new Contact()
                                .name("Mini Supermarket Team")
                                .email("vvqhuy1999@gmail.com")
                                .url("https://github.com/vvqhuy1999/mini_Supermarket"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("Development Server"),
                        new Server().url("https://api.minisupermarket.com").description("Production Server")
                ));
    }
} 