package ca.jrvs.apps.trading.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Siddharth Joshi",
                        email = "siddharthjoshi329@gmail.com"
                ),
                description = "Spring Boot Backend for Stockquote Application",
                title = "Stockquote Application",
                version = "1.0.0"
        )
)
public class SwaggerConfig {

}