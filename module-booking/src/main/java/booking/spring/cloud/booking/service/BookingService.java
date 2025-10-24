package booking.spring.cloud.booking.service;

import booking.spring.cloud.booking.clients.HotelManagementClient;
import booking.spring.cloud.booking.mapper.BookingMapper;
import booking.spring.cloud.booking.repository.BookingRepository;
import booking.spring.cloud.core.model.dto.BookingRequest;
import booking.spring.cloud.core.model.dto.BookingResponse;
import booking.spring.cloud.core.model.dto.Status;
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

    private final UserService userService;
    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final HotelManagementClient httpClient;

    public List<BookingResponse> getAll() {
        System.out.println(httpClient.getRecommend(1L, LocalDate.now()));
        final var user = userService.getCurrentUser();
        return repository.findByUser(user).stream()
                .map(mapper::entityToDto)
                .toList();
    }

    public BookingResponse getById(Long id) {
        final var user = userService.getCurrentUser();
        return repository.findByIdAndUser(id, user)
                .map(mapper::entityToDto)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingResponse saveBooking(BookingRequest booking) {
        var entity = mapper.dtoToEntity(booking);
        entity.setUser(userService.getCurrentUser());
        entity = repository.save(entity);

        final var hotel = httpClient.findByName(booking.hotel())
                .orElseThrow(() -> new IllegalArgumentException("Отель не найден"));

        final var room = httpClient.findRoomByNumber(hotel.id(), booking.room())
                .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));

        final var success = booking.start().datesUntil(booking.finish())
                .allMatch(date -> httpClient.confirmAvailability(room.id(), date).isPresent());

        entity.setStatus(success ? Status.CONFIRMED : Status.CANCELLED);
        entity = repository.save(entity);

        return mapper.entityToDto(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteBooking(Long id) {
        final var booking = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));

        final var user = userService.getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new IllegalStateException("Удалить можно только свое бронирование");
        }

        final var hotel = httpClient.findByName(booking.getHotel())
                .orElseThrow(() -> new IllegalArgumentException("Отель не найден"));

        final var room = httpClient.findRoomByNumber(hotel.id(), booking.getRoom())
                .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));

        if (Status.CONFIRMED.equals(booking.getStatus())) {
            booking.getStart().datesUntil(booking.getFinish())
                    .forEach(date -> httpClient.releaseRoom(room.id(), date));
        }

        repository.deleteById(id);

        return true;
    }
}
