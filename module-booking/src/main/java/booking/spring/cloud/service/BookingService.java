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

    private final BookingRepository bookingRepository;

    public List<BookingResponse> getAll() {
        return bookingRepository.findAll().stream()
                .map(BookingMapper.MAPPER::entityToResponse)
                .toList();
    }

    public BookingResponse getById(Long id) {
        return bookingRepository.findById(id)
                .map(BookingMapper.MAPPER::entityToResponse)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingResponse saveBooking(BookingRequest booking) {
        return BookingMapper.MAPPER.entityToResponse(
                bookingRepository.save(BookingMapper.MAPPER.requestToEntity(booking)));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteBooking(Long id) {
        if (bookingRepository.findById(id).isEmpty()) {
            return false;
        }

        bookingRepository.deleteById(id);

        return true;
    }
}
