package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Violation>> handleValidationException(MethodArgumentNotValidException ex) {
        List<Violation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(violations);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Internal server error", ex);

        Violation violation = new Violation(
                "ID",
                "Некорректный ввод данных: " + ex.getMessage()
        );

        return ResponseEntity
                .internalServerError()
                .body(new ValidationErrorResponse(List.of(violation)));
    }
}