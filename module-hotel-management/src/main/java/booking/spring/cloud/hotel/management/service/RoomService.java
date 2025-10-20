package booking.spring.cloud.hotel.management.service;

import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomDto;
import booking.spring.cloud.hotel.management.mapper.ReservationMapper;
import booking.spring.cloud.hotel.management.mapper.RoomMapper;
import booking.spring.cloud.hotel.management.entities.Hotel;
import booking.spring.cloud.hotel.management.entities.Reservation;
import booking.spring.cloud.hotel.management.entities.Room;
import booking.spring.cloud.hotel.management.repository.HotelRepository;
import booking.spring.cloud.hotel.management.repository.ReservationRepository;
import booking.spring.cloud.hotel.management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final RoomMapper roomMapper;
    private final ReservationMapper reservationMapper;

    public List<RoomDto> getAll() {
        return roomRepository.findAll().stream()
                .map(roomMapper::entityToDto)
                .toList();
    }

    public RoomDto getById(final Long id) {
        return roomRepository.findById(id)
                .map(roomMapper::entityToDto)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoomDto save(RoomDto dto) {
        var entity = roomMapper.dtoToEntity(dto);
        entity = roomRepository.save(entity);
        return roomMapper.entityToDto(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean delete(Long id) {
        if (roomRepository.findById(id).isEmpty()) {
            return false;
        }

        roomRepository.deleteById(id);

        return true;
    }

    public List<RoomDto> getRecommend(Long hotelId, LocalDate date) {
        return hotelRepository.findById(hotelId)
                .map(Hotel::getRooms)
                .orElseThrow(NullPointerException::new).stream()
                .filter(room -> checkAvailableByDate(room, date))
                .sorted(Comparator.comparingInt(Room::getTimes_booked).reversed())
                .map(roomMapper::entityToDto)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReservationDto confirmAvailability(Long roomId, LocalDate date) {
        final var room = roomRepository.findById(roomId)
                .orElseThrow(NullPointerException::new);
        if (!checkAvailableByDate(room, date)) {
            throw new IllegalStateException("Дата занята!");
        }

        final var reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setHotel(room.getHotel());
        reservation.setDate(date);
        reservationRepository.save(reservation);

        room.setTimes_booked(room.getTimes_booked() + 1);
        roomRepository.save(room);

        return reservationMapper.entityToDto(reservation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean release(Long roomId, LocalDate date) {
        final var room = roomRepository.findById(roomId)
                .orElseThrow(NullPointerException::new);
        final var reservation = room.getReservations().stream()
                .filter(r -> date.isEqual(r.getDate()))
                .findAny();
        if (reservation.isEmpty()) {
            return false;
        }

        reservationRepository.delete(reservation.get());

        return true;
    }

    private boolean checkAvailableByDate(Room room, LocalDate date) {
        return room.getReservations().stream()
                .map(Reservation::getDate)
                .anyMatch(date::isEqual);
    }
}
