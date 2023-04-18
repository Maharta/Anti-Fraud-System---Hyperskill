package antifraud.presentation.controller.exceptionhandler;

import antifraud.presentation.DTO.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        ValidationError validationError = processFieldErrors(fieldErrors);
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    private ValidationError processFieldErrors(List<FieldError> fieldErrors) {
        ValidationError validationError = new ValidationError(HttpStatus.BAD_REQUEST.value(), "Field Validation Error");
        for (FieldError fieldError : fieldErrors) {
            validationError.addFieldError(fieldError.getObjectName(), fieldError.getField(), fieldError.getField() + " " + fieldError.getDefaultMessage());
        }
        return validationError;
    }


    static class ValidationError {
        private final int status;
        private final String message;
        private List<FieldError> fieldErrors = new ArrayList<>();

        ValidationError(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public void addFieldError(String path, String field, String message) {
            FieldError error = new FieldError(path, field, message);
            fieldErrors.add(error);
        }

        public List<FieldError> getFieldErrors() {
            return fieldErrors;
        }
    }
}
