package booking.spring.cloud.booking.rest;

import booking.spring.cloud.booking.service.UserService;
import booking.spring.cloud.core.model.dto.UserRequest;
import booking.spring.cloud.core.model.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "REST API: Управление пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Список всех пользователей")
    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        log.info("Список всех пользователей");
        return userService.getAll();
    }

    @Operation(summary = "Информация о пользователе")
    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(@PathVariable Long id) {
        log.info("Информация о пользователе");
        return userService.getById(id);
    }

    @Operation(summary = "Удаление пользователя")
    @DeleteMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@RequestBody @Valid UserRequest user) {
        log.info("Удаление пользователя");
        if (userService.deleteUser(user)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Добавить роль ADMIN пользователю")
    @PutMapping("/{id}/set-admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void setAdmin(@PathVariable("id") Long id) {
        log.info("Добавление роли ADMIN пользователю");
        userService.setAdmin(id);
    }
}
