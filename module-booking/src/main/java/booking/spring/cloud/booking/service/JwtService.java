package booking.spring.cloud.booking.service;

import booking.spring.cloud.booking.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_CLAIMS_LOGIN;
import static booking.spring.cloud.core.model.utils.Constants.AUTH_CLAIMS_ROLE;

/**
 * Сервис для JWT
 */
@Service
public class JwtService {

    // Уникальный ключ для генерации токена
    @Value("${security.token.signing.key}")
    private String jwtSigningKey;

    // Время жизни токена в миллисекундах
    @Value("${security.token.expiration.minutes}")
    private int jwtExpirationMinutes;

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    public String generateToken(UserDetails userDetails) {
        var claims = new HashMap<String, Object>();
        if (userDetails instanceof User customUserDetails) {
            claims.put(AUTH_CLAIMS_LOGIN, customUserDetails.getUsername());
            claims.put(AUTH_CLAIMS_ROLE, customUserDetails.getRole());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final var userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final var claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерация токена
     *
     * @param extraClaims дополнительные данные
     * @param userDetails данные пользователя
     * @return токен
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date currentTime = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .claims().add(extraClaims).and()
                .subject(userDetails.getUsername())
                .issuedAt(currentTime)
                .expiration(DateUtils.addMinutes(currentTime, jwtExpirationMinutes))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Получение ключа для подписи токена
     *
     * @return ключ
     */
    private SecretKey getSigningKey() {
        final var keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getCurrentKey() {
        return Encoders.BASE64.encode(getSigningKey().getEncoded());
    }
}
