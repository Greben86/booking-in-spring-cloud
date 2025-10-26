package booking.spring.cloud.hotel.management.repository;

import booking.spring.cloud.hotel.management.entities.Hotel;
import booking.spring.cloud.hotel.management.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByHotelAndNumber(Hotel hotel, String name);
}
