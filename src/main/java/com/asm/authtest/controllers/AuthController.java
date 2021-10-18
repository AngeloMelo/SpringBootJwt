package com.asm.authtest.controllers;

import com.asm.authtest.models.User;
import com.asm.authtest.security.util.JWTDecoder;
import com.asm.authtest.security.util.JWTEncoder;
import com.asm.authtest.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import static com.asm.authtest.security.util.JWTConstants.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController
{
    private final UserService userService;

    @Autowired
    private JWTEncoder jwtEncoder;

    @Autowired
    private JWTDecoder jwtDecoder;

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        validateHeader(authorizationHeader);

        Map<String, String> resultTokens = new HashMap<>();

        try
        {
            String userName = jwtDecoder.getUserNameFromToken(authorizationHeader);
            Optional<User> userOptional = userService.getUser(userName);

            if(userOptional.isEmpty())
            {
                throw new RuntimeException("User not found");
            }

            User user = userOptional.get();
            List<String> roles = user.getRoles();

            String url = request.getRequestURL().toString();
            String accessToken = jwtEncoder.createToken(user.getUserName(), url, getAccessTokenExpirationAmount(), roles);

            resultTokens.put("refresh_token", accessToken);
        }
        catch (Exception ex)
        {
            response.setHeader("error", ex.getMessage());
            response.setStatus (FORBIDDEN.value());

            resultTokens.put("error_message", ex.getMessage());
        }

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), resultTokens);
    }

    private void validateHeader(String authorizationHeader)
    {
        if(authorizationHeader == null ||
                !authorizationHeader.startsWith(BEARER))
        {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    //TODO read from a config file?
    private Long getAccessTokenExpirationAmount()
    {
        return 2L * 60 * 1000;
    }
}
