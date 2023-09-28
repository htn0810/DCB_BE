package com.bosch.digicore.properties;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "euaa", ignoreUnknownFields = false)
public class EuaaProperties {

    private List<String> corsOrigins;
    private Security security = new Security();

    @Getter
    @Setter
    public static class Security {

        private RSAKey privateJwtKey;

        private RSAPublicKey publicJwtKey;
    }
}
