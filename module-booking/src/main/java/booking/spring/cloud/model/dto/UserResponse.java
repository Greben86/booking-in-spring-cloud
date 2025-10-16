package booking.spring.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Пользователь")
public record UserResponse(
        @Schema(description = "Имя пользователя", example = "Вася")
        @Size(min = 1, max = 50, message = "Имя пользователя должно содержать от 1 до 50 символов")
        @NotBlank(message = "Поле не может быть пустым")
        String username) {
}
