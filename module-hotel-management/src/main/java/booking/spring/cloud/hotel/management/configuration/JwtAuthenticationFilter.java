package booking.spring.cloud.hotel.management.configuration;

import booking.spring.cloud.hotel.management.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_BEARER_PREFIX;
import static booking.spring.cloud.core.model.utils.Constants.AUTH_HEADER_NAME;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Получаем токен из заголовка
        final var authHeader = request.getHeader(AUTH_HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, AUTH_BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Обрезаем префикс и получаем имя пользователя из токена
        final var jwt = authHeader.substring(AUTH_BEARER_PREFIX.length());
        final var username = jwtService.extractUserName(jwt);

        if (StringUtils.isNotEmpty(username)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Если токен валиден, то аутентифицируем пользователя
            if (!jwtService.isTokenExpired(jwt)) {
                final var context = SecurityContextHolder.createEmptyContext();

                final var authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singleton(jwtService.extractUserRole(jwt))
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }

}
