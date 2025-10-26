package booking.spring.cloud.booking.mapper;

import booking.spring.cloud.core.model.dto.BookingRequest;
import booking.spring.cloud.core.model.dto.BookingResponse;
import booking.spring.cloud.booking.entities.Booking;
import booking.spring.cloud.core.model.dto.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = Status.class)
public interface BookingMapper {

    @Mapping(target = "status", expression = "java(Status.PENDING)")
    @Mapping(target = "created_at", expression = "java(LocalDateTime.now())")
    Booking dtoToEntity(BookingRequest dto);

    BookingResponse entityToDto(Booking entity);
}
