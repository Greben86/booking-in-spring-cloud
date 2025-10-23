package booking.spring.cloud.booking.clients;

import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@FeignClient(name = "MODULE-HOTEL-MANAGEMENT", configuration = JwtAuthClientConfig.class)
public interface HotelManagementClient {

    @GetMapping("/api/hotels/hotel/{id}")
    Optional<HotelResponse> getHotel(@PathVariable("id") Long id);

    @GetMapping("/api/hotels/hotel/{hotelId}/recommend")
    List<RoomDto> getRecommend(@PathVariable("hotelId") Long hotelId,
                               @RequestParam(name = "date")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @GetMapping("/api/rooms/room/{id}")
    Optional<RoomDto> getRoom(@PathVariable("id") Long id);

    @GetMapping("/api/rooms/{roomId}/confirm-availability")
    Optional<ReservationDto> confirmAvailability(@PathVariable("roomId") Long roomId,
                                                 @RequestParam(name = "date")
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @GetMapping("/api/rooms/{roomId}/release")
    Optional<ReservationDto> releaseRoom(@PathVariable("roomId") Long roomId,
                                         @RequestParam(name = "date")
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}
