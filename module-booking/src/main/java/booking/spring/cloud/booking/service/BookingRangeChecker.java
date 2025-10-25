package booking.spring.cloud.booking.service;

import booking.spring.cloud.booking.entities.Booking;
import booking.spring.cloud.booking.repository.BookingRepository;
import booking.spring.cloud.core.model.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingRangeChecker {

    private final BookingRepository repository;

    // Основной метод проверки наличия пересечения с существующими диапазонами
    public boolean isOverlapping(Booking newBooking) {
        final var bookings = repository
                .findByHotelAndRoom(newBooking.getHotel(), newBooking.getRoom());
        for (final var booking : bookings) {
            if (!Status.CANCELLED.equals(booking.getStatus())
                    && intersects(newBooking, booking)) {
                return true; // Найдено пересечение
            }
        }

        return false; // Нет пересечения ни с одним из существующих диапазонов
    }


    // Проверка пересечения текущего диапазона с другим диапазоном
    private boolean intersects(Booking left, Booking right) {
        return !(left.getFinish().isBefore(right.getStart())
                || left.getStart().isAfter(right.getFinish()));
    }
}
