package booking.spring.cloud.booking.clients;

import booking.spring.cloud.booking.model.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient("MODULE-HOTEL-MANAGEMENT")
public interface HotelManagementClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/hotels/hotel/{hotelId}/recommend")
    List<RoomDto> getRecommend(@PathVariable("hotelId") Long hotelId,
                               @RequestParam(name = "date")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}
