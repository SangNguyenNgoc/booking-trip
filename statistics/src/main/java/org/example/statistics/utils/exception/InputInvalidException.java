package org.example.statistics.utils.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class InputInvalidException extends AbstractException {
    public InputInvalidException(List<String> messages) {
        super("Input invalid!", HttpStatus.BAD_REQUEST, messages);
    }
}
