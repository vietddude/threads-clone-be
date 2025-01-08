package viet.io.threadsbe.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.access-exp}")
    private long accessExp;

    @Value("${spring.jwt.refresh-exp}")
    private long refreshExp;
}