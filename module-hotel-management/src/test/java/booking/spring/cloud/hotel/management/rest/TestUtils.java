package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.core.model.dto.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_CLAIMS_LOGIN;
import static booking.spring.cloud.core.model.utils.Constants.AUTH_CLAIMS_ROLE;

public final class TestUtils {

    private TestUtils() {}

    public static String generateToken(String login, UserRole role, String jwtSigningKey) {
        final var currentTime = new Date(System.currentTimeMillis());
        final var keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Jwts.builder()
                .claims()
                .add(Map.of(AUTH_CLAIMS_LOGIN, login, AUTH_CLAIMS_ROLE, role))
                .and()
                .subject(login)
                .issuedAt(currentTime)
                .expiration(DateUtils.addMinutes(currentTime, 1))
                .signWith(Keys.hmacShaKeyFor(keyBytes), Jwts.SIG.HS256)
                .compact();
    }

    @SneakyThrows
    public static HotelResponse getHotelFromAnswer(final byte[] answer) {
        final var str = new String(answer, StandardCharsets.UTF_8);
        final var objectMapper = new ObjectMapper();
        try (final var response = objectMapper.createParser(str)) {
            return response.readValueAs(HotelResponse.class);
        }
    }
}
