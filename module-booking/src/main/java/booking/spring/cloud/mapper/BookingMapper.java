package booking.spring.cloud.mapper;

import booking.spring.cloud.model.dto.BookingRequest;
import booking.spring.cloud.model.dto.BookingResponse;
import booking.spring.cloud.model.entities.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "status", expression = "java(Status.PENDING)")
    BookingEntity requestToEntity(BookingRequest dto);

    BookingResponse entityToResponse(BookingEntity entity);
}
