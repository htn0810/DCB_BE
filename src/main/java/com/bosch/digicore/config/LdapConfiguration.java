package com.bosch.digicore.config;

import com.bosch.digicore.properties.LdapProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@RequiredArgsConstructor
public class LdapConfiguration {

	private final LdapProperties ldapProperties;

	@Bean
	public LdapContextSource ldapContextSource() {
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(ldapProperties.getUrl());
		ldapContextSource.setUserDn(ldapProperties.getUserDn());
		ldapContextSource.setPassword(ldapProperties.getPassword());
		ldapContextSource.setBase(ldapProperties.getBase());
		ldapContextSource.setBaseEnvironmentProperties(ldapProperties.getBaseEnvironmentProperties());
		ldapContextSource.afterPropertiesSet();

		return ldapContextSource;
	}

	@Bean
	public LdapTemplate ldapTemplate(LdapContextSource ldapContextSource) {
		return new LdapTemplate(ldapContextSource);
	}
}
