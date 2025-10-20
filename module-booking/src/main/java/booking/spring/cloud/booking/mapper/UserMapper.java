package booking.spring.cloud.booking.mapper;

import booking.spring.cloud.core.model.dto.UserRequest;
import booking.spring.cloud.core.model.dto.UserResponse;
import booking.spring.cloud.booking.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User dtoToEntity(UserRequest dto);

    UserResponse entityToDto(User entity);
}