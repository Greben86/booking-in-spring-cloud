package booking.spring.cloud.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос апартаментов отеля")
public record RoomRequest(
        @Schema(description = "Идентификатор отеля", example = "1")
        @NotNull
        long hotelId,
        @Schema(description = "Номер апартаментов отеля", example = "313")
        @Size(min = 1, max = 10, message = "Номер апартаментов отеля должно содержать от 1 до 10 символов")
        @NotBlank(message = "Номер апартаментов отеля не может быть пустыми")
        String number) {
}
