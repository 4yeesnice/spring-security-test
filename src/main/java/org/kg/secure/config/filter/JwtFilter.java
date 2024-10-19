package org.kg.secure.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${spring.security.secret}")
    String jwtSecret = "gsync";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestedApi = request.getServletPath();
        logger.info(requestedApi);
        if (requestedApi.equals("/api/v1/login") || requestedApi.equals("/api/v1/registry")) {
            filterChain.doFilter(request, response);
        } else {
            String token = request.getHeader("Authorization");
            if (token != null && !token.trim().isEmpty() && token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length());

                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                JWTVerifier verifier = JWT.require(algorithm).build();

                DecodedJWT decodedJWT = verifier.verify(token);

                String username = decodedJWT.getSubject();
                List<? extends GrantedAuthority> authorities = decodedJWT
                        .getClaims()
                        .get("authorities")
                        .asList(SimpleGrantedAuthority.class);

                logger.info(authorities);

                String tokenType = decodedJWT
                        .getClaims()
                        .get("type_of_token")
                        .asString();

                if (tokenType.equals("refresh") && !requestedApi.equals("/api/refresh")) {
                    // throw new BadTokenException("Refresh token used incorrectly");
                }

                var usernameToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(usernameToken);

                filterChain.doFilter(request, response);
            }
        }
    }



}
