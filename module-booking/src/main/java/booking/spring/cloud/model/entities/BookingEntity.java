package booking.spring.cloud.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Date;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @ManyToOne
    private UserEntity user;

    @Column
    private String hotel;

    @Column(length = 32)
    private String hotelRef;

    @Column(length = 10)
    private String room;

    private Date start;

    private Date finish;

    private Status status;
}
