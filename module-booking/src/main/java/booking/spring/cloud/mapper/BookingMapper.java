package booking.spring.cloud.mapper;

import booking.spring.cloud.model.dto.BookingRequest;
import booking.spring.cloud.model.dto.BookingResponse;
import booking.spring.cloud.model.entities.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {
    BookingMapper MAPPER = Mappers.getMapper(BookingMapper.class);

    BookingEntity requestToEntity(BookingRequest dto);

    BookingResponse entityToResponse(BookingEntity entity);
}
