package booking.spring.cloud.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Токен доступа")
public record JwtAuthenticationResponse(
        @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
        @NotBlank
        String token) {
}
