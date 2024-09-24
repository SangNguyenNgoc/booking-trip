package org.example.profile.untils.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class DataNotFoundException extends AbstractException {
    public DataNotFoundException(List<String> messages) {
        super("Data not found!", HttpStatus.NOT_FOUND, messages);
    }
}
