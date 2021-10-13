package com.asm.authtest.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.asm.authtest.security.util.JWTConstants.BEARER;

@Getter
@Component
@Scope("singleton")
public class JWTDecoder
{
    @Autowired
    JWTAlgorithmBuilder algorithmBuilder;

    public String getUserNameFromToken(String token)
    {
        String refreshToken = token.substring(BEARER.length());
        Algorithm algorithm = algorithmBuilder.getAlgorithm();
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        return decodedJWT.getSubject();
    }
}
