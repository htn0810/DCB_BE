package com.bosch.digicore.security;

import com.bosch.digicore.constants.Roles;
import com.bosch.digicore.exceptions.NotBoschUserException;
import com.bosch.digicore.exceptions.TokenExpiredException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @SneakyThrows
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) {
        try {
            String token = getTokenFromRequest(request);

            if (token != null) {
                final SignedJWT signedJWT = SignedJWT.parse(token);
                validateToken(signedJWT);
                final UserPrincipal userPrincipal = createUserPrincipal(signedJWT);
                final Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, userPrincipal.getUsername(), userPrincipal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }

    private String getTokenFromRequest(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void validateToken(final SignedJWT signedJWT) throws ParseException {
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expireTime.before(new Date())) {
            throw new TokenExpiredException();
        }
        String username = signedJWT.getJWTClaimsSet().getStringClaim("preferred_username");
        if (!username.contains("@bosch.com")) {
            throw new NotBoschUserException(username);
        }
    }

    private UserPrincipal createUserPrincipal(final SignedJWT signedJWT) throws ParseException {
        String username = signedJWT.getJWTClaimsSet().getStringClaim("preferred_username");
        String name = signedJWT.getJWTClaimsSet().getStringClaim("name");
        String email = signedJWT.getJWTClaimsSet().getStringClaim("email");
        List<String> roles = signedJWT.getJWTClaimsSet().getStringListClaim("roles");
        List<GrantedAuthority> authorities = roles == null ? Collections.singletonList(new SimpleGrantedAuthority(Roles.ROLE_USER)) :
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UserPrincipal(username, name, email, authorities);
    }
}
