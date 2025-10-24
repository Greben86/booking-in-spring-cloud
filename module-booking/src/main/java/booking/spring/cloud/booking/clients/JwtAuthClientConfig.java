package booking.spring.cloud.booking.clients;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_HEADER_NAME;

@Slf4j
@Configuration
public class JwtAuthClientConfig {

    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return requestTemplate -> {
            final var headerValue = (String) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getDetails();
            log.info("Заголовок авторизации: {}", headerValue);
            requestTemplate.header(AUTH_HEADER_NAME, headerValue);
        };
    }
}
