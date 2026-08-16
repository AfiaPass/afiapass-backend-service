package org.afiapass.gateway.gatway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI afiaPassOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("AfiaPass API Gateway")
                        .description("The transit permit and tax-routing infrastructure powering the AfiaPass ecosystem. Bridges REST clients with the Stellar Soroban network.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("John-Daniel Ikechukwu")
                                .url("https://github.com/johdanike")) // Update with your actual handle
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")));
    }
}