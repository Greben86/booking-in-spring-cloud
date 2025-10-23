package booking.spring.cloud.booking.service;

import booking.spring.cloud.booking.entities.User;
import booking.spring.cloud.booking.mapper.UserMapper;
import booking.spring.cloud.booking.repository.UserRepository;
import booking.spring.cloud.core.model.dto.UserRequest;
import booking.spring.cloud.core.model.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    public User register(final UserRequest user, final String encodedPassword) {
        if (repository.findByUsername(user.username()).isPresent()) {
            throw new IllegalStateException("Такой уже есть");
        }

        var entity = mapper.dtoToEntity(user);
        entity.setPassword(encodedPassword);
        return repository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteUser(final UserRequest user) {
        if (repository.findByUsername(user.username()).isEmpty()) {
            return false;
        }

        repository.delete(mapper.dtoToEntity(user));

        return true;
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public User getByUsername(final String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public User getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        final var username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return getByUsername(username);
    }
}
