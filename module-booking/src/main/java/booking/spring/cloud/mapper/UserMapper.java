package booking.spring.cloud.mapper;

import booking.spring.cloud.model.dto.UserRequest;
import booking.spring.cloud.model.dto.UserResponse;
import booking.spring.cloud.model.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    UserEntity requestToEntity(UserRequest dto);

    UserResponse entityToResponse(UserEntity entity);
}