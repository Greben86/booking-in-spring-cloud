package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomRequest;
import booking.spring.cloud.core.model.dto.RoomResponse;
import booking.spring.cloud.hotel.management.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse save(@RequestBody @Valid RoomRequest room) {
        log.info("Сохранение апартаментов");
        return service.save(room);
    }

    @Operation(summary = "Удаление апартаментов")
    @DeleteMapping("/room/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Удаление апартаментов");
        if (service.delete(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Поиск апартаментов по номеру")
    @GetMapping("/find-by-hotel/{hotelId}")
    @ResponseStatus(HttpStatus.OK)
    public RoomResponse findRoomByNumber(@PathVariable Long hotelId,
                                         @RequestParam(name = "number") String number) {
        log.info("Поиск апартаментов по номеру");
        return service.findByHotelAndNumber(hotelId, number);
    }

    @Operation(summary = "Получить список свободных апартаментов на дату")
    @GetMapping("/room/recommend-by-hotel/{hotelId}")
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getRecommend(@PathVariable Long hotelId,
                                           @RequestParam(name = "date")
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Список свободных апартаментов на дату");
        return service.getRecommend(hotelId, date);
    }

    @Operation(summary = "Установка доступности апартаментов")
    @PutMapping("/room/{id}/set-available")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void setAvailable(@PathVariable Long id) {
        log.info("Установлена доступность апартаментов");
        service.setAvailable(id);
    }

    @Operation(summary = "Отмена доступности апартаментов")
    @PutMapping("/room/{id}/unset-available")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void unsetAvailable(@PathVariable Long id) {
        log.info("Отменена доступность апартаментов");
        service.unsetAvailable(id);
    }

    @PutMapping("/room/{id}/confirm-availability")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto confirmAvailability(@PathVariable("id") Long roomId,
                                              @RequestParam(name = "requestId") String requestId,
                                              @RequestParam(name = "start")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                              @RequestParam(name = "end")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        log.info("Бронирование апартаментов");
        return service.confirmAvailability(requestId, roomId, start, end);
    }

    @DeleteMapping("/room/{id}/release")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> release(@PathVariable("id") Long roomId,
                                        @RequestParam(name = "requestId") String requestId) {
        log.info("Снятие брони с апартаментов");
        if (service.release(requestId, roomId)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
