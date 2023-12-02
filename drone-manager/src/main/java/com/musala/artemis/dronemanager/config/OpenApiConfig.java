package com.musala.artemis.dronemanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "batyrk",
                        email = "kineevbatyr@gmail.com",
                        url = "https://example.com"
                ),
                description = "OpenApi documentation for Drone Manager",
                title = "Drone Manager",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://example.com"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Local environment",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Prod environment",
                        url = "https://example.com"
                )
        }
)
public class OpenApiConfig {
}
