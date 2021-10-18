package com.asm.authtest.filter;

import com.asm.authtest.requestmodels.UsernameAndPassword;
import com.asm.authtest.security.util.JWTEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private final AuthenticationManager authenticationManager;
    private final JWTEncoder jwtEncoder;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JWTEncoder jwtEncoder)
    {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException
    {
        try
        {
            UsernameAndPassword usernameAndPassword = getUserNameAndPassword(request);
            String userName = usernameAndPassword.getUsername();
            String password = usernameAndPassword.getPassword();
            log.info("User name is {}", userName);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, password);
            log.info("authToken is {}", authToken);

            return authenticationManager.authenticate(authToken);
        }
        catch (IOException e)
        {
            throw new AuthenticationException(e.getMessage()){};
        }
    }

    private UsernameAndPassword getUserNameAndPassword(HttpServletRequest request) throws IOException
    {
        String body = request.getReader().lines().collect(Collectors.joining());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, UsernameAndPassword.class);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
            throws IOException
    {
        User user = (User)authentication.getPrincipal();

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String url = request.getRequestURL().toString();

        String accessToken = jwtEncoder.createToken(user.getUsername(), url, getAccessTokenExpirationAmount(), roles);
        String refreshToken = jwtEncoder.createToken(user.getUsername(), url, getRefreshTokenExpirationAmount(), new ArrayList<>());

        /*
        response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refreshToken);
        */
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
     }

    //TODO read from a config file?
    private Long getAccessTokenExpirationAmount()
    {
        return 2L * 60 * 1000;
    }

    //TODO read from a config file?
    private Long getRefreshTokenExpirationAmount()
    {
        return 30L * 60 * 1000;
    }
}
