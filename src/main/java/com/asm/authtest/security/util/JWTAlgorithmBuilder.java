package com.asm.authtest.security.util;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class JWTAlgorithmBuilder
{
    private String secret;
    private Algorithm algorithm;

    public JWTAlgorithmBuilder()
    {
        //TODO get from config file?
        this.secret = "msecret";
        this.algorithm = Algorithm.HMAC256(this.secret.getBytes());
    }

    public Algorithm getAlgorithm()
    {
        return algorithm;
    }

}
