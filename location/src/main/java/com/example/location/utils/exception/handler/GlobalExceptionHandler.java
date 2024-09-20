package com.example.location.utils.exception.handler;

import com.example.location.utils.dtos.ErrorResponse;
import com.example.location.utils.exception.AbstractException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorResponse> handleException(final AbstractException exception) {
        return buildResponse(exception);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleException(final DataIntegrityViolationException exception) {
        var status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .httpStatus(status)
                        .statusCode(status.value())
                        .error("Dữ liệu bị trùng lắp !")
                        .build()
        );
    }


    public ResponseEntity<ErrorResponse> buildResponse(AbstractException e) {
        return ResponseEntity.status(e.getStatus().value()).body(
                ErrorResponse.builder()
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .httpStatus(e.getStatus())
                        .statusCode(e.getStatus().value())
                        .error(e.getError())
                        .messages(e.getMessages())
                        .build()
        );
    }
}
