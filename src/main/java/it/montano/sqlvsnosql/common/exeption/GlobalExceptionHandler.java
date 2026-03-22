package it.montano.sqlvsnosql.common.exeption;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
          MethodArgumentNotValidException ex,
          HttpServletRequest request) {

    var errors = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .toList();

    return ResponseEntity.badRequest().body(
            new ErrorResponse(
                    400,
                    "Bad Request",
                    String.join(", ", errors),
                    request.getRequestURI(),
                    LocalDateTime.now()
            )
    );
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
          ResourceNotFoundException ex,
          HttpServletRequest request) {

    return ResponseEntity.status(404).body(
            new ErrorResponse(
                    404,
                    "Not Found",
                    ex.getMessage(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            )
    );
  }

  @ExceptionHandler({
          org.springframework.dao.DataIntegrityViolationException.class,
          com.mongodb.DuplicateKeyException.class
  })
  public ResponseEntity<ErrorResponse> handleDuplicate(
          Exception ex,
          HttpServletRequest request) {

    return ResponseEntity.status(409).body(
            new ErrorResponse(
                    409,
                    "Conflict",
                    "Resource already exists",
                    request.getRequestURI(),
                    LocalDateTime.now()
            )
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(
          Exception ex,
          HttpServletRequest request) {

    return ResponseEntity.status(500).body(
            new ErrorResponse(
                    500,
                    "Internal Server Error",
                    ex.getMessage(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            )
    );
  }
}