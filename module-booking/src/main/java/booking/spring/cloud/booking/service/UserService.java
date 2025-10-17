package booking.spring.cloud.booking.service;

import booking.spring.cloud.booking.mapper.UserMapper;
import booking.spring.cloud.booking.model.dto.UserRequest;
import booking.spring.cloud.booking.model.dto.UserResponse;
import booking.spring.cloud.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .toList();
    }

    public UserResponse getById(final Long id) {
        return repository.findById(id)
                .map(mapper::entityToDto)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserResponse register(final UserRequest user) {
        if (repository.findByUsername(user.username()).isPresent()) {
            throw new IllegalStateException("Такой уже есть");
        }

        var entity = mapper.dtoToEntity(user);
        entity = repository.save(entity);
        return mapper.entityToDto(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteUser(final UserRequest user) {
        if (repository.findByUsername(user.username()).isEmpty()) {
            return false;
        }

        repository.delete(mapper.dtoToEntity(user));

        return true;
    }
}
