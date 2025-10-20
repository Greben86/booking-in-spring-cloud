package booking.spring.cloud.hotel.management.mapper;

import booking.spring.cloud.core.model.dto.HotelRequest;
import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.hotel.management.entities.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelMapper {

    HotelResponse entityToDto(Hotel hotel);

    Hotel dtoToEntity(HotelRequest dto);
}
