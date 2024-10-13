package org.example.vehicle.clients;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.example.vehicle.utils.exception.DataNotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is4xxClientError()) {
            return new DataNotFoundException(List.of("Location not found"));
        } else {
            return new Exception("Generic exception");
        }
    }
}
