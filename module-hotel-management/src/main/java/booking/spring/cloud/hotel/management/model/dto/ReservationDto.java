package booking.spring.cloud.hotel.management.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Schema(description = "Бронирование номера отеля")
public record ReservationDto(
        @Schema(description = "Идентификатор отеля", example = "1")
        @NotBlank(message = "Идентификатор отеля не может быть пустыми")
        long hotelId,
        @Schema(description = "Номер апартаментов отеля", example = "313")
        @Size(min = 1, max = 10, message = "Номер апартаментов отеля должно содержать от 1 до 10 символов")
        @NotBlank(message = "Номер апартаментов отеля не может быть пустыми")
        String roomNumber,
        @Schema(description = "Дата и час начала бронирования", pattern = "dd-MM-yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Moscow")
        @NotNull(message = "Дата и час начала бронирования не может быть пустым")
        Date start,
        @Schema(description = "Дата и час окончания бронирования", pattern = "dd-MM-yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Moscow")
        @NotNull(message = "Дата и час окончания бронирования не может быть пустым")
        Date finish) {
}
