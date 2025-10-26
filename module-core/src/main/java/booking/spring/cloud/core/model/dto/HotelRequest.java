package booking.spring.cloud.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Отель")
public record HotelRequest(
        @Schema(description = "Название отеля", example = "Хилтон")
        @Size(min = 1, max = 255, message = "Название отеля должно содержать от 1 до 255 символов")
        @NotBlank(message = "Название отеля не может быть пустыми")
        String name) {
}
