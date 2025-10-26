package booking.spring.cloud.hotel.management.service;

import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomRequest;
import booking.spring.cloud.core.model.dto.RoomResponse;
import booking.spring.cloud.hotel.management.mapper.ReservationMapper;
import booking.spring.cloud.hotel.management.mapper.RoomMapper;
import booking.spring.cloud.hotel.management.entities.Hotel;
import booking.spring.cloud.hotel.management.entities.Reservation;
import booking.spring.cloud.hotel.management.entities.Room;
import booking.spring.cloud.hotel.management.repository.HotelRepository;
import booking.spring.cloud.hotel.management.repository.ReservationRepository;
import booking.spring.cloud.hotel.management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final TraceIdHolder traceIdHolder;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final RoomMapper roomMapper;
    private final ReservationMapper reservationMapper;

    public List<RoomResponse> getAll() {
        return roomRepository.findAll().stream()
                .map(roomMapper::entityToDto)
                .toList();
    }

    public RoomResponse getById(final Long id) {
        return roomRepository.findById(id)
                .map(roomMapper::entityToDto)
                .orElse(null);
    }

    public RoomResponse findByHotelAndNumber(Long hotelId, final String number) {
        final var hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Нет такого отеля"));
        return roomRepository.findByHotelAndNumber(hotel, number)
                .map(roomMapper::entityToDto)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoomResponse save(RoomRequest dto) {
        final var hotel = hotelRepository.findById(dto.hotelId())
                .orElseThrow(() -> new IllegalArgumentException("Нет такого отеля"));
        var entity = roomMapper.dtoToEntity(dto);
        entity.setHotel(hotel);
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

    public List<RoomResponse> getRecommend(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .map(Hotel::getRooms)
                .orElseThrow(NullPointerException::new).stream()
                .sorted(Comparator.comparingInt(Room::getTimes_booked).reversed())
                .map(roomMapper::entityToDto)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReservationDto confirmAvailability(String requestId, Long roomId, LocalDate start, LocalDate finish) {
        final var room = roomRepository.findById(roomId)
                .orElseThrow(NullPointerException::new);
        if (!room.isAvailable()) {
            throw new IllegalStateException("Апартаменты не доступны!");
        }

        final var reservation = new Reservation();
        reservation.setRequestId(requestId);
        reservation.setRoom(room);
        reservation.setHotel(room.getHotel());
        reservation.setStart(start);
        reservation.setFinish(finish);
        reservationRepository.save(reservation);

        room.setTimes_booked(room.getTimes_booked() + 1);
        roomRepository.save(room);
        log.info("{} Апартаменты забронированы", traceIdHolder.getTraceId());

        return reservationMapper.entityToDto(reservation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean release(String requestId, Long roomId) {
        final var room = roomRepository.findById(roomId)
                .orElseThrow(NullPointerException::new);
        final var reservation = room.getReservations().stream()
                .filter(r -> requestId.equals(r.getRequestId()))
                .findAny();
        if (reservation.isEmpty()) {
            return false;
        }

        reservationRepository.delete(reservation.get());
        log.info("{} Бронь снята", traceIdHolder.getTraceId());

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setAvailable(Long id) {
        final var room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Нет таких апартаментов!"));

        room.setAvailable(true);
        roomRepository.save(room);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void unsetAvailable(Long id) {
        final var room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Нет таких апартаментов!"));

        room.setAvailable(false);
        roomRepository.save(room);
    }

    public List<RoomResponse> popularRooms() {
        return roomRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Room::getTimes_booked).reversed())
                .map(roomMapper::entityToDto)
                .toList();
    }
}
