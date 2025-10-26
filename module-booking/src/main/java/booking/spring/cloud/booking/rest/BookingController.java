package booking.spring.cloud.booking.rest;

import booking.spring.cloud.booking.service.BookingService;
import booking.spring.cloud.core.model.dto.BookingRequest;
import booking.spring.cloud.core.model.dto.BookingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/bookings")
@RequiredArgsConstructor
@Tag(name = "REST API: Управление бронированием")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Список бронирований пользователя")
    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getAll() {
        log.info("Посмотреть все бронирования пользователя");
        return bookingService.getAll();
    }

    @Operation(summary = "Бронирование пользователя по ID")
    @GetMapping("/booking/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getById(@PathVariable Long id) {
        log.info("Запрос бронирования по ID");
        return bookingService.getById(id);
    }

    @Operation(summary = "Зарегистрировать бронирование")
    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse addBooking(@RequestBody @Valid BookingRequest booking) {
        log.info("Регистрация бронирования");
        return bookingService.addBooking(booking);
    }

    @Operation(summary = "Удалить бронирование")
    @DeleteMapping("/booking/{id}")
    public ResponseEntity<Void> releaseBooking(@PathVariable Long id) {
        log.info("Удаление бронирования");
        if (bookingService.releaseBooking(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}