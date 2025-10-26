package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.hotel.management.service.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TraceRequestAdvice implements RequestBodyAdvice {

    private final TraceIdHolder traceIdHolder;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (inputMessage.getHeaders().containsKey("X-B3-TraceId")) {
            traceIdHolder.setTraceId(inputMessage.getHeaders().getFirst("X-B3-TraceId"));
        } else {
            traceIdHolder.setTraceId(UUID.randomUUID().toString());
        }
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
