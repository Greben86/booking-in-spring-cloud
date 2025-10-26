package booking.spring.cloud.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Бронирование номера отеля")
public record ReservationDto(
        @Schema(description = "Уникальный идентификатор запроса", example = "123")
        @Size(min = 1, max = 255, message = "Уникальный идентификатор запроса должен содержать от 1 до 255 символов")
        @NotBlank(message = "Уникальный идентификатор запроса не может быть пустыми")
        String requestId,
        @Schema(description = "Идентификатор отеля", example = "1")
        @NotBlank(message = "Идентификатор отеля не может быть пустыми")
        long hotelId,
        @Schema(description = "Идентификатор номера", example = "1")
        @NotBlank(message = "Идентификатор номера не может быть пустыми")
        long roomId,
        @Schema(description = "Номер апартаментов отеля", example = "313")
        @Size(min = 1, max = 10, message = "Номер апартаментов отеля должно содержать от 1 до 10 символов")
        @NotBlank(message = "Номер апартаментов отеля не может быть пустыми")
        String roomNumber,
        @Schema(description = "Дата начала бронирования", pattern = "dd-MM-yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Moscow")
        @NotNull(message = "Дата начала бронирования не может быть пустой")
        LocalDate start,
        @Schema(description = "Дата окончания бронирования", pattern = "dd-MM-yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Moscow")
        @NotNull(message = "Дата окончания бронирования не может быть пустой")
        LocalDate finish) {
}
