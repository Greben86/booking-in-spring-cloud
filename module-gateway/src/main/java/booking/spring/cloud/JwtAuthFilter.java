package booking.spring.cloud;

import booking.spring.cloud.core.model.utils.Constants;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Configuration> {

    public JwtAuthFilter() {
        super(Configuration.class);
    }

    @Override
    public GatewayFilter apply(Configuration configuration) {
        return (exchange, chain) -> {
            final var path = exchange.getRequest().getPath().toString();
            final var authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (!path.contains("/api/auth/sign/")
                    && (authHeader == null || !authHeader.startsWith(Constants.AUTH_BEARER_PREFIX))) {
                log.error("Нет токена авторизации");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            }

            final var traceId = UUID.randomUUID().toString();
            log.info("TraceId = {}", traceId);
            final var mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-B3-TraceId", traceId)
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    public static final class Configuration {
    }
}