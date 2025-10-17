package booking.spring.cloud.hotel.management.repository;

import booking.spring.cloud.hotel.management.model.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
