package org.kg.secure.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.kg.secure.controller.Users;
import org.kg.secure.dto.LoginDto;
import org.kg.secure.response.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenCreate {

//    @Value("${spring.security.secret}")
    private String secret = "gsync";

//    @Value("${spring.security.token_lifetime}")
    private Long duration = 300000L;

    public TokenResponse create(Users user){

        Algorithm algorithm = Algorithm.HMAC256(secret);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String generatedAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .withClaim("authorities", roles)
                .withClaim("type_of_token", "access")
                .sign(algorithm);

        String refreshAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withNotBefore(new Date(System.currentTimeMillis() + duration))
                .withExpiresAt(new Date(System.currentTimeMillis() + (duration * 2)))
                .withClaim("authorities", roles)
                .withClaim("type_of_token", "refresh")
                .sign(algorithm);

        TokenResponse tokenResponse = TokenResponse
                .builder()
                .accessToken(generatedAccessToken)
                .refreshToken(refreshAccessToken)
                .build();

        return tokenResponse;
    }

}
