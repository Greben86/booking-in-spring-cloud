package booking.spring.cloud.booking.entities;

import booking.spring.cloud.core.model.dto.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Column
    private String requestId;

    @ManyToOne
    private User user;

    @Column
    private String hotel;

    @Column(length = 10)
    private String room;

    @Column
    private LocalDate start;

    @Column
    private LocalDate finish;

    @Column
    private Status status;

    @Column
    private LocalDateTime created_at;
}
