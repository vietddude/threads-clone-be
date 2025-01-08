package viet.io.threadsbe.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import viet.io.threadsbe.config.JwtConfig;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class JwtService {
    private static final long MILLISECONDS_IN_A_MINUTE = 1000 * 60;
    private JwtConfig config;

    private String genToken(UUID subjectId, long expiration) {
        return JWT.create()
                .withSubject(subjectId.toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration * MILLISECONDS_IN_A_MINUTE))
                .sign(Algorithm.HMAC256(config.getSecret()));
    }

    public String genAccessToken(UUID userId) {
        return genToken(userId, config.getAccessExp());
    }

    public String genRefreshToken(UUID userId) {
        return genToken(userId, config.getRefreshExp());
    }

    public UUID extractUserId(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return UUID.fromString(jwt.getSubject());
    }

    public boolean isTokenValid(String token, UUID userId) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(config.getSecret()))
                    .build()
                    .verify(token);

            String jwtUserId = jwt.getSubject();
            Date expiresAt = jwt.getExpiresAt();

            return UUID.fromString(jwtUserId).equals(userId) && expiresAt.after(new Date());
        } catch (JWTVerificationException e) {
            log.error("JWT verification failed: {}", e.getMessage());
            return false;
        }
    }
}
