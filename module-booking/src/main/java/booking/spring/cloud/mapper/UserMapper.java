package booking.spring.cloud.mapper;

import booking.spring.cloud.model.dto.UserRequest;
import booking.spring.cloud.model.dto.UserResponse;
import booking.spring.cloud.model.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserEntity requestToEntity(UserRequest dto);

    UserResponse entityToResponse(UserEntity entity);
}