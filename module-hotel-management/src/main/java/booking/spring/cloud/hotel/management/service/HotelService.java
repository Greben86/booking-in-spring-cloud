package booking.spring.cloud.hotel.management.service;

import booking.spring.cloud.core.model.dto.HotelRequest;
import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.hotel.management.mapper.HotelMapper;
import booking.spring.cloud.hotel.management.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository repository;
    private final HotelMapper mapper;

    public List<HotelResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .toList();
    }

    public HotelResponse getById(final Long id) {
        return repository.findById(id)
                .map(mapper::entityToDto)
                .orElse(null);
    }

    public HotelResponse findByName(final String name) {
        return repository.findByName(name)
                .map(mapper::entityToDto)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public HotelResponse save(HotelRequest dto) {
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
