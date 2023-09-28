package com.bosch.digicore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "bearer-jwt", description = "Bearer Token", bearerFormat = "JWT",
		scheme = "bearer", in = SecuritySchemeIn.HEADER, paramName = "Authorization")
@OpenAPIDefinition(info = @Info(title = "DigiCore REST API", version = "1.4",
		description = "<a href=\"http://https://inside-docupedia.bosch.com/confluence/x/1Fr8sw\" target=\"_blank\">How to integrate with Digital Core</a>"),
		security = @SecurityRequirement(name = "bearer-jwt"))
public class DigiCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigiCoreApplication.class, args);
	}
}
