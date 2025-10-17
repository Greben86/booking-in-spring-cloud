package booking.spring.cloud.hotel.management.mapper;

import booking.spring.cloud.hotel.management.model.dto.ReservationDto;
import booking.spring.cloud.hotel.management.model.entities.Hotel;
import booking.spring.cloud.hotel.management.model.entities.Reservation;
import booking.spring.cloud.hotel.management.model.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    @Mapping(target = "hotelId", expression = "java(reservation.getHotel().getId())")
    @Mapping(target = "roomNumber", expression = "java(reservation.getRoom().getNumber())")
    ReservationDto entityToDto(Reservation reservation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", source = "hotelEntity")
    @Mapping(target = "room", source = "roomEntity")
    Reservation dtoToEntity(ReservationDto dto, Hotel hotelEntity, Room roomEntity);
}
