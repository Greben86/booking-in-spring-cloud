package booking.spring.cloud.hotel.management.repository;

import booking.spring.cloud.hotel.management.model.entities.Hotel;
import booking.spring.cloud.hotel.management.model.entities.Reservation;
import booking.spring.cloud.hotel.management.model.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByHotel(Hotel hotel);

    List<Reservation> findByRoom(Room hotel);
}
