package booking.spring.cloud.hotel.management.service;

import booking.spring.cloud.hotel.management.mapper.ReservationMapper;
import booking.spring.cloud.hotel.management.model.dto.ReservationDto;
import booking.spring.cloud.hotel.management.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;
    private final HotelService hotelService;
    private final RoomService roomService;
    private final ReservationMapper mapper;

    public List<ReservationDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .toList();
    }

    public ReservationDto getById(final Long id) {
        return repository.findById(id)
                .map(mapper::entityToDto)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReservationDto save(ReservationDto dto) {
        final var hotel = hotelService.getByIdOrElseThrow(dto.hotelId());
        final var room = roomService.getByHotelAndNumberOrElseThrow(hotel, dto.roomNumber());
        var entity = mapper.dtoToEntity(dto, hotel, room);
        entity = repository.save(entity);
        return mapper.entityToDto(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            return false;
        }

        repository.deleteById(id);

        return true;
    }
}
