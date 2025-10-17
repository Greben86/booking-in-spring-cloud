package booking.spring.cloud.hotel.management.service;

import booking.spring.cloud.hotel.management.mapper.RoomMapper;
import booking.spring.cloud.hotel.management.model.dto.RoomDto;
import booking.spring.cloud.hotel.management.model.entities.Hotel;
import booking.spring.cloud.hotel.management.model.entities.Room;
import booking.spring.cloud.hotel.management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository repository;
    private final RoomMapper mapper;

    public List<RoomDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .toList();
    }

    public RoomDto getById(final Long id) {
        return repository.findById(id)
                .map(mapper::entityToDto)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Room getByHotelAndNumberOrElseThrow(Hotel hotel, String number) {
        return repository.findByHotelAndNumber(hotel, number)
                .orElseThrow(NullPointerException::new);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoomDto save(RoomDto dto) {
        var entity = mapper.dtoToEntity(dto);
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
