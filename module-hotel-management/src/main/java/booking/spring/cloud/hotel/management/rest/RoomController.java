package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.hotel.management.model.dto.RoomDto;
import booking.spring.cloud.hotel.management.service.RoomService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService service;

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<RoomDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/room/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoomDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.OK)
    public RoomDto save(@RequestBody RoomDto booking) {
        return service.save(booking);
    }

    @DeleteMapping("/room/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
