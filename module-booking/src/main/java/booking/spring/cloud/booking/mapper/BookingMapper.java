package booking.spring.cloud.booking.mapper;

import booking.spring.cloud.booking.model.dto.BookingRequest;
import booking.spring.cloud.booking.model.dto.BookingResponse;
import booking.spring.cloud.booking.model.entities.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "status", expression = "java(Status.PENDING)")
    Booking dtoToEntity(BookingRequest dto);

    BookingResponse entityToDto(Booking entity);
}
