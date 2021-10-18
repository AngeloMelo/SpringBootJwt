package com.asm.authtest.filter;

import com.asm.authtest.security.util.JWTDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.asm.authtest.security.util.JWTConstants.BEARER;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter
{
    private final JWTDecoder jwtDecoder;
    public CustomAuthorizationFilter(JWTDecoder jwtDecoder)
    {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        if(request.getServletPath().equals("/api/login") ||
           request.getServletPath().equals("/api/token/refresh"))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader == null || !authorizationHeader.startsWith(BEARER))
        {
            filterChain.doFilter(request, response);
        }

        try
        {
            String userName = jwtDecoder.getUserNameFromToken(authorizationHeader);

            //TODO implement role based authorization
            //String [] roles = decodedJWT.getClaim("roles").asArray(String.class);
            String [] roles = new String[0];
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            stream(roles).forEach(role ->
                    authorities.add(new SimpleGrantedAuthority(role))
            );

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        }
        catch (Exception ex)
        {
            log.error("Error on handling token {} ", ex.getMessage());
            response.setHeader("error", ex.getMessage());
            response.setStatus (FORBIDDEN.value());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("error_message", ex.getMessage());

            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
    }
}
