package com.asm.authtest.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.asm.authtest.security.util.JWTConstants.ROLES;

@Component
@Scope("singleton")
public class JWTEncoder
{
    @Autowired
    JWTAlgorithmBuilder algorithmBuilder;

    public String createToken(String subject, String issuer, Long expirationAmount, List<String> roles)
    {
        Algorithm algorithm = algorithmBuilder.getAlgorithm();

        return JWT.create()
            .withSubject(subject)
            .withExpiresAt(new Date(System.currentTimeMillis() + expirationAmount))
            .withIssuer(issuer)
            .withClaim(ROLES, roles)
            .sign(algorithm);
    }
}
