package antifraud.presentation.controller.exceptionhandler;

import antifraud.business.exception.EntityNotFoundException;
import antifraud.business.exception.RoleConflictException;
import antifraud.presentation.DTO.error.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        ValidationError validationError = processFieldErrors(fieldErrors);
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationError> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ValidationError validationError = processConstraintViolations(constraintViolations);
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RoleConflictException.class, EntityExistsException.class})
    public ResponseEntity<ErrorResponseDTO> handleExistingConflict(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(EntityNotFoundException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ValidationError processFieldErrors(List<FieldError> fieldErrors) {
        ValidationError validationError = new ValidationError(HttpStatus.BAD_REQUEST.value(), "Field Validation Error");
        for (FieldError fieldError : fieldErrors) {
            validationError.addCustomFieldError(fieldError.getField(),
                    fieldError.getDefaultMessage(),
                    fieldError.getRejectedValue());
        }
        return validationError;
    }

    private ValidationError processConstraintViolations(Set<ConstraintViolation<?>> constraintViolations) {
        ValidationError validationError = new ValidationError(HttpStatus.BAD_REQUEST.value(), "PathVariable Validation Error");
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            validationError.addCustomFieldError(constraintViolation.getPropertyPath().toString(),
                    constraintViolation.getMessage(),
                    constraintViolation.getInvalidValue());
        }

        return validationError;
    }


    static class ValidationError {
        private final int statusCode;
        private final String message;
        private final List<CustomFieldError> fieldErrors = new ArrayList<>();

        ValidationError(int status, String message) {
            this.statusCode = status;
            this.message = message;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void addCustomFieldError(String field, String message, Object rejectedValue) {
            CustomFieldError error = new CustomFieldError(field, message, rejectedValue);
            fieldErrors.add(error);
        }

        public List<CustomFieldError> getFieldErrors() {
            return fieldErrors;
        }

        record CustomFieldError(String field, String message, Object rejectedValue) {
        }
    }
}
