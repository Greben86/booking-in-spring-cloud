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

    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserResponse getById(final Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserResponse register(final UserRequest user) {
        if (repository.findByUsername(user.username()).isPresent()) {
            throw new IllegalStateException("Такой уже есть");
        }

        var entity = mapper.requestToEntity(user);
        entity = repository.save(entity);
        return mapper.entityToResponse(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteUser(final UserRequest user) {
        if (repository.findByUsername(user.username()).isEmpty()) {
            return false;
        }

        repository.delete(mapper.requestToEntity(user));

        return true;
    }
}
