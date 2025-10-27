package booking.spring.cloud.booking.rest;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Контроллер для перехвата исключений
 */
@RestControllerAdvice
public class ErrorAdvice {

    /**
     * Если поймали исключение валидации {@link MethodArgumentNotValidException}, то возвращаем статус 400
     *
     * @return ответ со статусом 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, List<String>> handleValidationErrors(final MethodArgumentNotValidException ex) {
        final var fieldErrors = ex.getFieldErrors().stream()
                .filter(error -> error.getDefaultMessage() != null)
                .map(error -> Pair.of(error.getField(), error.getDefaultMessage()));
        final var globalErrors = ex.getGlobalErrors().stream()
                .filter(error -> error.getDefaultMessage() != null)
                .map(error -> Pair.of(error.getObjectName(), error.getDefaultMessage()));

        return Stream.concat(fieldErrors, globalErrors)
                .collect(Collectors.groupingBy(Pair::getFirst,
                        Collectors.mapping(Pair::getSecond, Collectors.toList())));
    }

    /**
     * Если поймали исключение {@link Exception}, то возвращаем статус 418
     *
     * @return ответ со статусом 418
     */
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler(Exception.class)
    public String handleLogicErrors(final Exception ex) {
        return ex.getMessage();
    }

}
