package booking.spring.cloud.hotel.management.repository;

import booking.spring.cloud.hotel.management.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
