package com.example.location.clients;

import com.example.location.utils.exception.DataNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.util.List;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is4xxClientError()) {
            return new DataNotFoundException(List.of(response.reason()));
        } else {
            return new Exception("Generic exception");
        }
    }
}
