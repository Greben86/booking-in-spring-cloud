package booking.spring.cloud.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Отель")
public record HotelResponse(
        @Schema(description = "Идентификатор отеля", example = "1")
        @NotBlank
        long id,
        @Schema(description = "Название отеля", example = "Хилтон")
        @Size(min = 1, max = 255, message = "Название отеля должно содержать от 1 до 255 символов")
        @NotBlank(message = "Название отеля не может быть пустыми")
        String name) {
}
