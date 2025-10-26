package booking.spring.cloud.booking.service;

import booking.spring.cloud.booking.clients.HotelManagementClient;
import booking.spring.cloud.booking.mapper.BookingMapper;
import booking.spring.cloud.booking.repository.BookingRepository;
import booking.spring.cloud.core.model.dto.BookingRequest;
import booking.spring.cloud.core.model.dto.BookingResponse;
import booking.spring.cloud.core.model.dto.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final UserService userService;
    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final HotelManagementClient httpClient;
    private final BookingRangeChecker bookingRangeChecker;

    public List<BookingResponse> getAll() {
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
    public BookingResponse addBooking(BookingRequest booking) {
        if (booking.start().isAfter(booking.finish()))
            throw new IllegalArgumentException("Начало периода бронирования должно быть раньше окончания");

        final var existsEntity = repository.findByRequestId(booking.requestId());
        if (existsEntity.isPresent()) {
            log.warn("Такой запрос уже обрабатывался");
            return mapper.entityToDto(existsEntity.get());
        }

        final var hotel = readBody(httpClient.findByName(booking.hotel()))
                .orElseThrow(() -> new IllegalArgumentException("Отель не найден"));

        final var room = readBody(httpClient.findRoomByNumber(hotel.id(), booking.room()))
                .orElseThrow(() -> new IllegalArgumentException("Апартаменты не найдены"));

        if (!room.available()) {
            throw new IllegalArgumentException("Апартаменты не доступны");
        }

        var entity = mapper.dtoToEntity(booking);
        if (bookingRangeChecker.isOverlapping(entity)) {
            throw new IllegalArgumentException("Найдено пересечение с другим бронированием");
        }
        entity.setUser(userService.getCurrentUser());
        entity = repository.save(entity);

        try {
            log.info("Блокировка апартаментов");
            final var success = readBody(httpClient
                    .confirmAvailability(room.id(), booking.requestId(), booking.start(), booking.finish()))
                    .isPresent();

            entity.setStatus(success ? Status.CONFIRMED : Status.CANCELLED);
            entity = repository.save(entity);

            return mapper.entityToDto(entity);
        } catch (Exception e) {
            log.error("Компенсация блокировки апартаментов", e);
            httpClient.releaseRoom(room.id(), booking.requestId());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean releaseBooking(Long id) {
        final var booking = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));

        final var user = userService.getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new IllegalStateException("Удалить можно только свое бронирование");
        }

        final var hotel = readBody(httpClient.findByName(booking.getHotel()))
                .orElseThrow(() -> new IllegalArgumentException("Отель не найден"));

        final var room = readBody(httpClient.findRoomByNumber(hotel.id(), booking.getRoom()))
                .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));

        log.info("Компенсация блокировки апартаментов");
        httpClient.releaseRoom(room.id(), booking.getRequestId());

        repository.deleteById(id);

        return true;
    }

    private <T> Optional<T> readBody(ResponseEntity<T> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Status code={}, body={}", response.getStatusCode().value(), response.getBody());
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
