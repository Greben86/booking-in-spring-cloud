package booking.spring.cloud.booking.service;

import booking.spring.cloud.booking.clients.HotelManagementClient;
import booking.spring.cloud.booking.mapper.BookingMapper;
import booking.spring.cloud.booking.model.dto.BookingRequest;
import booking.spring.cloud.booking.model.dto.BookingResponse;
import booking.spring.cloud.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final HotelManagementClient httpClient;

    public List<BookingResponse> getAll() {
        System.out.println(httpClient.getRecommend(1L, LocalDate.now()));
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .toList();
    }

    public BookingResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToDto)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingResponse saveBooking(BookingRequest booking) {

        var entity = mapper.dtoToEntity(booking);
        entity = repository.save(entity);
        return mapper.entityToDto(entity);
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
