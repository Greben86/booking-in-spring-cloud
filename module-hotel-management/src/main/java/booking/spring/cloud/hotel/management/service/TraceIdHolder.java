package booking.spring.cloud.hotel.management.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class TraceIdHolder {

    @Setter
    @Getter
    private String traceId;
}
