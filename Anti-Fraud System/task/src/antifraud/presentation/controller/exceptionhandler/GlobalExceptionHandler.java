package antifraud.presentation.controller.exceptionhandler;

import antifraud.business.exception.EntityNotFoundException;
import antifraud.business.exception.IPAlreadyExistException;
import antifraud.business.exception.RoleConflictException;
import antifraud.business.exception.UsernameTakenException;
import antifraud.presentation.DTO.error.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<ValidationError> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        ValidationError validationError = processFieldErrors(fieldErrors);
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UsernameTakenException.class, RoleConflictException.class, IPAlreadyExistException.class})
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
            System.out.println(fieldError.getRejectedValue());
            validationError.addFieldError(fieldError.getField(), fieldError.getField() + " " + fieldError.getDefaultMessage(), fieldError.getRejectedValue());
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

        public void addFieldError(String field, String message, Object rejectedValue) {
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
