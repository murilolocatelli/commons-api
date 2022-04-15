package com.example.commons.api.filter.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private String jwtSecretKey;

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String HEADER_AUTHORIZATION = "Authorization";

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecretKey) {
        super(authenticationManager);
        this.jwtSecretKey = jwtSecretKey;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String headerAuthorizationValue = request.getHeader(HEADER_AUTHORIZATION);

        if (headerAuthorizationValue == null || !headerAuthorizationValue.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = this.getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(
        HttpServletRequest request) throws UnsupportedEncodingException {

        String headerAuthorizationValue = request.getHeader(HEADER_AUTHORIZATION);

        String jwtSecretKeyBase64 =
            Base64.getEncoder().encodeToString(jwtSecretKey.getBytes(StandardCharsets.UTF_8.displayName()));

        if (headerAuthorizationValue != null) {
            try {
                Claims data = Jwts.parser()
                    .setSigningKey(jwtSecretKeyBase64)
                    .parseClaimsJws(headerAuthorizationValue.replace(TOKEN_PREFIX, ""))
                    .getBody();

                if (data != null) {
                    return new UsernamePasswordAuthenticationToken(data, null, new ArrayList<>());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            return null;
        }
        return null;
    }

}
