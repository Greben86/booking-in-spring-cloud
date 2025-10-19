package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.hotel.management.model.dto.HotelRequest;
import booking.spring.cloud.hotel.management.model.dto.HotelResponse;
import booking.spring.cloud.hotel.management.model.dto.RoomDto;
import booking.spring.cloud.hotel.management.service.HotelService;
import booking.spring.cloud.hotel.management.service.RoomService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService service;
    private final RoomService roomService;

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<HotelResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/hotel/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HotelResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/hotel")
    @ResponseStatus(HttpStatus.OK)
    public HotelResponse save(@RequestBody HotelRequest booking) {
        return service.save(booking);
    }

    @DeleteMapping("/hotel/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/hotel/{id}/recommend")
    @ResponseStatus(HttpStatus.OK)
    public List<RoomDto> getRecommend(@PathVariable Long id,
                                      @RequestParam(name = "date")
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return roomService.getRecommend(id, date);
    }
}
