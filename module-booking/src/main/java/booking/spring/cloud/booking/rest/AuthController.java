package booking.spring.cloud.booking.rest;

import booking.spring.cloud.booking.service.AuthenticationService;
import booking.spring.cloud.booking.service.JwtService;
import booking.spring.cloud.core.model.dto.JwtAuthenticationResponse;
import booking.spring.cloud.core.model.dto.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/auth/sign")
@RequiredArgsConstructor
@Tag(name = "REST API: Аутентификация")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Operation(summary = "Регистрация пользователя")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/up",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse signUp(@RequestBody @Valid UserRequest request) {
        log.info("Регистрация пользователя {}", request.username());
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/in",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse signIn(@RequestBody @Valid UserRequest request) {
        log.info("Авторизация пользователя {}", request.username());
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Ключ авторизации пользователя")
    @GetMapping("/key")
    @ResponseStatus(HttpStatus.OK)
    public String getKey() {
        return jwtService.getCurrentKey();
    }
}
