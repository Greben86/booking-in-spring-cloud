package booking.spring.cloud.service;

import booking.spring.cloud.mapper.UserMapper;
import booking.spring.cloud.model.dto.UserRequest;
import booking.spring.cloud.model.dto.UserResponse;
import booking.spring.cloud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper.MAPPER::entityToResponse)
                .toList();
    }

    public UserResponse getById(final Long id) {
        return userRepository.findById(id)
                .map(UserMapper.MAPPER::entityToResponse)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserResponse register(final UserRequest user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new IllegalStateException("Такой уже есть");
        }

        return UserMapper.MAPPER.entityToResponse(
                userRepository.save(UserMapper.MAPPER.requestToEntity(user)));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteUser(final UserRequest user) {
        if (userRepository.findByUsername(user.username()).isEmpty()) {
            return false;
        }

        userRepository.delete(UserMapper.MAPPER.requestToEntity(user));

        return true;
    }
}
