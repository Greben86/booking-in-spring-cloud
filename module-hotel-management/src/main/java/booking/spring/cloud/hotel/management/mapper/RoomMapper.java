package booking.spring.cloud.hotel.management.mapper;

import booking.spring.cloud.core.model.dto.RoomRequest;
import booking.spring.cloud.core.model.dto.RoomResponse;
import booking.spring.cloud.hotel.management.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {

    @Mapping(target = "hotelId", expression = "java(room.getHotel().getId())")
    RoomResponse entityToDto(Room room);

    @Mapping(target = "available", expression = "java(Boolean.TRUE)")
    @Mapping(target = "times_booked", constant = "0")
    Room dtoToEntity(RoomRequest dto);
}
