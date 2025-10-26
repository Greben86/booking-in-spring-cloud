package booking.spring.cloud.booking.clients;

import booking.spring.cloud.booking.service.TraceIdHolder;
import feign.RequestInterceptor;
import feign.Retryer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_HEADER_NAME;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    private final TraceIdHolder traceIdHolder;

    /**
     * Добавление заголовка авторизации
     */
    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return requestTemplate -> {
            final var headerValue = (String) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getDetails();
            log.info("{} Заголовок авторизации: {}", traceIdHolder.getTraceId(), headerValue);
            requestTemplate.header("X-B3-TraceId", traceIdHolder.getTraceId());
            requestTemplate.header(AUTH_HEADER_NAME, headerValue);
        };
    }

    /**
     * Повторение запросов в случае ошибки
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 2000, 3);
    }
}
