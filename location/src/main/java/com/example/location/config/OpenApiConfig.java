package com.example.location.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "Bearer Authentication",
        description = "JWT auth description",
        type = SecuritySchemeType.OAUTH2,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER,
        flows = @io.swagger.v3.oas.annotations.security.OAuthFlows(
                authorizationCode = @io.swagger.v3.oas.annotations.security.OAuthFlow(
                        authorizationUrl = "http://localhost:9000/oauth2/authorize",
                        tokenUrl = "http://localhost:9000/oauth2/token",
                        scopes = {
                                @OAuthScope(name = "openid", description = "openid"),
                        },
                        extensions = {
                                @Extension(name = "x-pkce", properties = {
                                        @ExtensionProperty(name = "required", value = "true")
                                })
                        }
                )
        )
)
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Location service API")
                        .description("Location service API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .email("nngocsang38@gmail.com")
                                .url("https://github.com/SangNguyenNgoc")
                                .name("SangNguyenNgoc")
                        )
                )
                .servers(List.of(new Server()
                        .url("http://localhost:8082")
                        .description("Localhost"))
                );
    }
}
