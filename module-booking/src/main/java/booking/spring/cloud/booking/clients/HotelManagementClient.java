package booking.spring.cloud.booking.clients;

import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "MODULE-HOTEL-MANAGEMENT", configuration = FeignClientConfig.class)
public interface HotelManagementClient {

    @GetMapping("/api/hotels")
    List<HotelResponse> getHotels();

    @GetMapping("/api/hotels/hotel/{id}")
    ResponseEntity<HotelResponse> getHotel(@PathVariable("id") Long id);

    @GetMapping("/api/hotels/hotel/find")
    ResponseEntity<HotelResponse> findByName(@RequestParam(name = "name") String name);

    @GetMapping("/api/hotels/hotel/{id}/room")
    ResponseEntity<RoomResponse> findRoomByNumber(@PathVariable Long id,
                                                  @RequestParam(name = "number") String number);

    @GetMapping("/api/hotels/hotel/{hotelId}/recommend")
    List<RoomResponse> getRecommend(@PathVariable("hotelId") Long hotelId,
                                    @RequestParam(name = "date")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @GetMapping("/api/rooms/room/{id}")
    ResponseEntity<RoomResponse> getRoom(@PathVariable("id") Long id);

    @GetMapping("/api/rooms/{roomId}/confirm-availability")
    ResponseEntity<ReservationDto> confirmAvailability(@PathVariable("roomId") Long roomId,
                                                       @RequestParam(name = "date")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @GetMapping("/api/rooms/{roomId}/release")
    ResponseEntity<ReservationDto> releaseRoom(@PathVariable("roomId") Long roomId,
                                               @RequestParam(name = "date")
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}
