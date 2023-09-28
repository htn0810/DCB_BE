package com.bosch.digicore.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ldap", ignoreUnknownFields = false)
public class LdapProperties {

    private String url;

    private String userDn;

    private String password;

    private String base;

    private Map<String, Object> baseEnvironmentProperties;
}
