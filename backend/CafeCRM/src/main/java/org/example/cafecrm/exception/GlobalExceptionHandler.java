package org.example.cafecrm.exception;

import lombok.NonNull;
import org.example.cafecrm.domain.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleCustomException(CustomException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getHttpStatus().value(),
                ex.getMessage(),
                ex.getHttpStatus().value()
        );

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleResourceNotFound(NotFoundException ex) {
        return handleCustomException(ex);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleConflict(ConflictException ex) {
        return handleCustomException(ex);
    }
}
