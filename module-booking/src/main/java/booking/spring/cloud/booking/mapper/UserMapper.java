package booking.spring.cloud.booking.mapper;

import booking.spring.cloud.core.model.dto.UserRequest;
import booking.spring.cloud.core.model.dto.UserResponse;
import booking.spring.cloud.booking.entities.User;
import booking.spring.cloud.core.model.dto.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = UserRole.class)
public interface UserMapper {

    @Mapping(target = "role", expression = "java(UserRole.ROLE_USER)")
    User dtoToEntity(UserRequest dto);

    UserResponse entityToDto(User entity);
}