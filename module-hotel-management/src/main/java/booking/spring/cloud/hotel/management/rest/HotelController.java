package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.core.model.dto.HotelRequest;
import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.hotel.management.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/hotels")
@RequiredArgsConstructor
@Tag(name = "REST API: Управление отелями")
public class HotelController {

    private final HotelService service;

    @Operation(summary = "Список отелей")
    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<HotelResponse> getAll() {
        log.info("Список отелей");
        return service.getAll();
    }

    @Operation(summary = "Информация о отеле по ID")
    @GetMapping("/hotel/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HotelResponse getById(@PathVariable Long id) {
        log.info("Информация о отеле по ID");
        return service.getById(id);
    }

    @Operation(summary = "Поиск отеля по названию")
    @GetMapping("/hotel/find")
    @ResponseStatus(HttpStatus.OK)
    public HotelResponse findByName(@RequestParam(name = "name") String name) {
        log.info("Поиск отеля по названию");
        return service.findByName(name);
    }

    @Operation(summary = "Сохранить отель")
    @PostMapping("/hotel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public HotelResponse save(@RequestBody @Valid HotelRequest booking) {
        log.info("Сохранение отеля");
        return service.save(booking);
    }

    @Operation(summary = "Удалить отель")
    @DeleteMapping("/hotel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Удаление отеля");
        if (service.delete(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
