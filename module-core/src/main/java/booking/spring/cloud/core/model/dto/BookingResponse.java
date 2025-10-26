package booking.spring.cloud.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Бронирование номера отеля")
public record BookingResponse(
        @Schema(description = "Идентификатор бронирования", example = "1")
        @NotNull
        Long id,
        @Schema(description = "Уникальный идентификатор запроса", example = "123")
        @Size(min = 1, max = 255, message = "Уникальный идентификатор запроса должен содержать от 1 до 255 символов")
        @NotBlank(message = "Уникальный идентификатор запроса не может быть пустыми")
        String requestId,
        @Schema(description = "Название отеля", example = "Хилтон")
        @Size(min = 1, max = 255, message = "Название отеля должно содержать от 1 до 255 символов")
        @NotBlank(message = "Название отеля не может быть пустыми")
        String hotel,
        @Schema(description = "Номер апартаментов", example = "313")
        @Size(min = 1, max = 10, message = "Номер апартаментов должен содержать от 1 до 255 символов")
        @NotBlank(message = "Номер апартаментов не может быть пустыми")
        String room,
        @Schema(description = "Дата и час начала бронирования", pattern = "dd-MM-yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Moscow")
        @NotNull(message = "Дата и час начала бронирования не может быть пустым")
        LocalDate start,
        @Schema(description = "Дата и час окончания бронирования", pattern = "dd-MM-yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Moscow")
        @NotNull(message = "Дата и час окончания бронирования не может быть пустым")
        LocalDate finish,
        @Schema(description = "Статус бронирования", example = "Ожидает")
        Status status,
        @Schema(description = "Дата и час окончания бронирования", pattern = "dd-MM-yyyy HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone="Europe/Moscow")
        LocalDateTime created_at) {
}
