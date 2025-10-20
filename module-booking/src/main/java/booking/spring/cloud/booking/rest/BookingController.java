package booking.spring.cloud.booking.rest;

import booking.spring.cloud.booking.service.BookingService;
import booking.spring.cloud.core.model.dto.BookingRequest;
import booking.spring.cloud.core.model.dto.BookingResponse;
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
@RequestMapping("api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getAll() {
        return bookingService.getAll();
    }
    
    @GetMapping("/booking/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getById(@PathVariable Long id) {
        return bookingService.getById(id);
    }

    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse saveBooking(@RequestBody BookingRequest booking) {
        return bookingService.saveBooking(booking);
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (bookingService.deleteBooking(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}