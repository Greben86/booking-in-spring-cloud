package booking.spring.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Schema(description = "Запрос бронирования номера отеля")
public record BookingRequest(
        @Schema(description = "Название отеля", example = "Хилтон")
        @Size(min = 1, max = 255, message = "Название отеля должно содержать от 1 до 255 символов")
        @NotBlank(message = "Название отеля не может быть пустыми")
        String hotel,
        @Schema(description = "Номер апартаментов", example = "my_1secret1_password")
        @Size(min = 1, max = 10, message = "Номер апартаментов должен содержать от 1 до 255 символов")
        @NotBlank(message = "Номер апартаментов не может быть пустыми")
        String room,
        @Schema(description = "Дата и час начала бронирования", pattern = "dd-MM-yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Moscow")
        @NotNull(message = "Дата и час начала бронирования не может быть пустым")
        Date start,
        @Schema(description = "Дата и час окончания бронирования", pattern = "dd-MM-yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Moscow")
        @NotNull(message = "Дата и час окончания бронирования не может быть пустым")
        Date finish) {
}
