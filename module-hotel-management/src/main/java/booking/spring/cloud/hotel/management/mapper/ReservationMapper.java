package booking.spring.cloud.hotel.management.mapper;

import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.hotel.management.entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    @Mapping(target = "hotelId", expression = "java(reservation.getHotel().getId())")
    @Mapping(target = "roomId", expression = "java(reservation.getRoom().getId())")
    @Mapping(target = "roomNumber", expression = "java(reservation.getRoom().getNumber())")
    ReservationDto entityToDto(Reservation reservation);
}
