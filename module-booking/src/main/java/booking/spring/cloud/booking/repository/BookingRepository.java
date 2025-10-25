package booking.spring.cloud.booking.repository;

import booking.spring.cloud.booking.entities.Booking;
import booking.spring.cloud.booking.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    Optional<Booking> findByIdAndUser(Long id, User user);

    List<Booking> findByHotelAndRoom(String hotel, String room);
}