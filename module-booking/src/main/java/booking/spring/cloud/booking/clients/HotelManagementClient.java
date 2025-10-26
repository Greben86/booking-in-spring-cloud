package booking.spring.cloud.booking.clients;

import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "MODULE-HOTEL-MANAGEMENT", configuration = FeignClientConfig.class)
public interface HotelManagementClient {

    @GetMapping("/api/hotels/hotel/find")
    ResponseEntity<HotelResponse> findByName(@RequestParam(name = "name") String name);

    @GetMapping("/api/rooms/find-by-hotel/{hotelId}")
    ResponseEntity<RoomResponse> findRoomByNumber(@PathVariable Long hotelId,
                                                  @RequestParam(name = "number") String number);

    @GetMapping("/api/rooms/room/recommend-by-hotel/{hotelId}")
    List<RoomResponse> getRecommend(@PathVariable Long hotelId,
                                    @RequestParam(name = "date")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);


    @PutMapping("/api/rooms/room/{roomId}/confirm-availability")
    ResponseEntity<ReservationDto> confirmAvailability(@PathVariable("roomId") Long roomId,
                                                       @RequestParam(name = "requestId") String requestId,
                                                       @RequestParam(name = "start")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                       @RequestParam(name = "end")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end);

    @DeleteMapping("/api/rooms/room/{roomId}/release")
    ResponseEntity<ReservationDto> releaseRoom(@PathVariable("roomId") Long roomId,
                                               @RequestParam(name = "requestId") String requestId);
}
