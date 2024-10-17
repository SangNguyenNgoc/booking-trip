package com.example.location.clients;

import com.example.location.utils.exception.DataNotFoundException;
import com.example.location.utils.exception.InputInvalidException;
import com.example.location.utils.exception.InternalServerException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.util.List;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());
        log.error("Error occurred when calling API: {} | Status code: {} | Reason: {}",
                methodKey, responseStatus.value(), response.reason());

        log.error("Full Response: Headers: {} | Body: {}",
                response.headers(),
                response.body() != null ? response.body().toString() : "No body");

        if (responseStatus.is4xxClientError()) {
            return switch (responseStatus) {
                case BAD_REQUEST -> {
                    log.error("Client error: Bad Request (400) - Invalid request from the client.");
                    yield new InputInvalidException(List.of("Input invalid"));
                }
                case UNAUTHORIZED -> {
                    log.error("Client error: Unauthorized (401) - Authentication failed.");
                    yield new InternalServerException(List.of("Unauthorized (401): Authentication required or failed."));
                }
                case FORBIDDEN -> {
                    log.error("Client error: Forbidden (403) - Access denied.");
                    yield new InternalServerException(List.of("Forbidden (403): Access denied."));
                }
                case NOT_FOUND -> {
                    log.error("Client error: Not Found (404) - Resource not found.");
                    yield new DataNotFoundException(List.of("Not Found (404): Resource not available."));
                }
                default -> {
                    log.error("Client error: Other 4xx error - Status code: {}", responseStatus.value());
                    yield new RuntimeException("Client error: " + responseStatus.value() + " - " + response.reason());
                }
            };
        }
        throw new InternalServerException(List.of("Internal Server Error"));
    }
}
