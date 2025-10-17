package booking.spring.cloud.service;

import booking.spring.cloud.mapper.BookingMapper;
import booking.spring.cloud.model.dto.BookingRequest;
import booking.spring.cloud.model.dto.BookingResponse;
import booking.spring.cloud.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;

    public List<BookingResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public BookingResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingResponse saveBooking(BookingRequest booking) {
        var entity = mapper.requestToEntity(booking);
        entity = repository.save(entity);
        return mapper.entityToResponse(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteBooking(Long id) {
        if (repository.findById(id).isEmpty()) {
            return false;
        }

        repository.deleteById(id);

        return true;
    }
}
