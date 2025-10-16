package booking.spring.cloud;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
//@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Configuration> {
    private final Cache<String, Key> keyCache = CacheBuilder.newBuilder()
            .weakValues()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .maximumSize(10)
            .recordStats()
            .build();
    private final RestTemplate restTemplate;

    public JwtAuthFilter() {
        super(Configuration.class);
        this.restTemplate = new RestTemplate();
    }

    private static UserInformation extractToken(String token, @NonNull Key key) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        return new UserInformation(body);
    }

    @Override
    public GatewayFilter apply(Configuration configuration) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            UserInformation userInformation = extractToken(token, getKey());
            if (userInformation.isValid()) {
                ServerHttpRequest mutatedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-User-Id", String.valueOf(userInformation.getUserId()))
                        .header("X-User-Name", userInformation.getLogin())
                        .header("X-User-Roles", String.join(",", userInformation.getRoles()))
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }

    @SneakyThrows
    private Key getKey() {
        return keyCache.get("SIGNING_KEY", () -> {
            ResponseEntity<String> key = restTemplate.getForEntity("http://user-relation-authentication:8080/key", String.class);
            var keyBytes = Decoders.BASE64.decode(key.getBody());
            return Objects.requireNonNull(Keys.hmacShaKeyFor(keyBytes));
        });
    }

    public static final class Configuration {
    }

    @Slf4j
    @Getter
    @ToString
    private static final class UserInformation {
        private final Long userId;
        private final String login;
        private final List<String> roles;

        private UserInformation(Claims claims) {
            this.userId = claims.get("user_id", Long.class);
            this.login = claims.get("username", String.class);
            this.roles = List.of(claims.get("roles", String.class).split(","));
        }

        public boolean isValid() {
            return getUserId() != null && getLogin() != null && getRoles() != null;
        }
    }
}