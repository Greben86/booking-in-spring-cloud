package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomRequest;
import booking.spring.cloud.core.model.dto.RoomResponse;
import booking.spring.cloud.hotel.management.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/rooms")
@RequiredArgsConstructor
@Tag(name = "REST API: Управление апартаментами")
public class RoomController {

    private final RoomService service;

    @Operation(summary = "Список апартаментов")
    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getAll() {
        log.info("Список апартаментов");
        return service.getAll();
    }

    @Operation(summary = "Информация о апартаментах по ID")
    @GetMapping("/room/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoomResponse getById(@PathVariable Long id) {
        log.info("Информация о апартаментах по ID");
        return service.getById(id);
    }

    @Operation(summary = "Сохранение апартаментов")
    @PostMapping("/room")
    @ResponseStatus(HttpStatus.OK)
    public RoomResponse save(@RequestBody RoomRequest room) {
        log.info("Сохранение апартаментов");
        return service.save(room);
    }

    @Operation(summary = "Удаление апартаментов")
    @DeleteMapping("/room/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Удаление апартаментов");
        if (service.delete(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/confirm-availability")
    @ResponseStatus(HttpStatus.OK)
    public ReservationDto confirmAvailability(@PathVariable Long id,
                                              @RequestParam(name = "date")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Бронирование апартаментов");
        return service.confirmAvailability(id, date);
    }

    @GetMapping("/{id}/release")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> release(@PathVariable Long id,
                                        @RequestParam(name = "date")
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Снятие брони с апартаментов");
        if (service.release(id, date)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
