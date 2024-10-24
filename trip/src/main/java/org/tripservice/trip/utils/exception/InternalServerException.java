package org.tripservice.trip.utils.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class InternalServerException extends AbstractException {
    public InternalServerException(List<String> messages) {
        super("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR, messages);
    }
}
