package com.bosch.digicore.config;

import com.bosch.digicore.properties.EuaaProperties;
import com.bosch.digicore.security.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final EuaaProperties euaaProperties;
	private final TokenAuthenticationFilter tokenAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http.csrf().disable()
				.cors().configurationSource(corsConfigurationSource())
				.and()
				.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers("/login/oauth2/code/**", "/oauth2/authorization/**").permitAll()	// allow these 2 paths for OAuth2 login via Azure AD
				.antMatchers("/api/public/**").permitAll()
				.antMatchers(HttpMethod.GET,"/api/images/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.oauth2Login().userInfoEndpoint().userAuthoritiesMapper(claimRolesToAuthoritiesMapper());
		return http.build();
	}

	private CorsConfigurationSource corsConfigurationSource() {
		final List<String> corsOrigins = euaaProperties.getCorsOrigins();
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(corsOrigins);
		configuration.setAllowedMethods(List.of("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("X-Total-Count"));
		configuration.setMaxAge(Duration.ofSeconds(4000));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/**
	 * This mapper is responsible for retrieving the roles claim from the OidcUserAuthority populated by the OidcUserService.
	 * As no user info endpoint is used, it is retrieved from the id token.
	 *
	 * @return an authorities mapper to be used within an UserInfoEndpointConfig
	 */
	private GrantedAuthoritiesMapper claimRolesToAuthoritiesMapper() {
		return authorities -> authorities.stream().filter(OidcUserAuthority.class::isInstance).flatMap(authority -> {
			final List<String> roles = ((OidcUserAuthority) authority).getIdToken().getClaimAsStringList("roles");
			return roles == null ? Stream.empty() : roles.stream().map(SimpleGrantedAuthority::new);
		}).collect(Collectors.toSet());
	}
}
