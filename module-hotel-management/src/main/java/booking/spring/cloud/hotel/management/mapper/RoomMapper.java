package booking.spring.cloud.hotel.management.mapper;

import booking.spring.cloud.core.model.dto.RoomDto;
import booking.spring.cloud.hotel.management.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {

    @Mapping(target = "hotelId", expression = "java(room.getHotel().getId())")
    RoomDto entityToDto(Room room);

    Room dtoToEntity(RoomDto dto);
}
