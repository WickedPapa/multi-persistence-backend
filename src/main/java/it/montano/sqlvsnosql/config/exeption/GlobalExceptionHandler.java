package it.montano.sqlvsnosql.config.exeption;

import it.montano.sqlvsnosql.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Formats bean validation errors into a consistent problem response.
   *
   * @param ex validation exception raised by Spring
   * @param request HTTP request context
   * @return 400 error payload with aggregated field errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    logException(request, ex, 400);

    var errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .toList();

    return ResponseEntity.badRequest()
        .body(
            new ErrorResponse(
                400,
                "Bad Request",
                String.join(", ", errors),
                request.getRequestURI(),
                OffsetDateTime.now()));
  }

  /**
   * Converts resource-not-found exceptions into a 404 problem document.
   *
   * @param ex domain exception indicating the missing resource
   * @param request HTTP request context
   * @return 404 error payload
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {

    logException(request, ex, 404);

    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(
                404, "Not Found", ex.getMessage(), request.getRequestURI(), OffsetDateTime.now()));
  }

  /**
   * Handles duplicate key violations (SQL or Mongo) consistently.
   *
   * @param ex persistence exception thrown by the driver
   * @param request HTTP request context
   * @return 409 error payload
   */
  @ExceptionHandler({
    org.springframework.dao.DataIntegrityViolationException.class,
    com.mongodb.DuplicateKeyException.class
  })
  public ResponseEntity<ErrorResponse> handleDuplicate(Exception ex, HttpServletRequest request) {

    logException(request, ex, 409);

    return ResponseEntity.status(409)
        .body(
            new ErrorResponse(
                409,
                "Conflict",
                "Resource already exists",
                request.getRequestURI(),
                OffsetDateTime.now()));
  }

  /**
   * Acts as a safety net for unexpected errors.
   *
   * @param ex uncaught exception
   * @param request HTTP request context
   * @return 500 error payload
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {

    logException(request, ex, 500);

    return ResponseEntity.status(500)
        .body(
            new ErrorResponse(
                500,
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()));
  }

  private void logException(HttpServletRequest request, Exception ex, Integer status) {
    log.error(
        "Handled exception:\n--------------------------------------------\nERROR:\nstatus = {}\npath = {}\nerror = {}\n--------------------------------------------",
        status,
        request.getRequestURI(),
        ex.getMessage(),
        ex);
  }
}
