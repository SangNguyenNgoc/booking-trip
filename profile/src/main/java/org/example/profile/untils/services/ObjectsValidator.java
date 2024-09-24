package org.example.profile.untils.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.profile.untils.exception.InputInvalidException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectsValidator<T> {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public void validate(T objectToValidate) {
        Set<ConstraintViolation<T>> validations = validator.validate(objectToValidate);
        if (!validations.isEmpty()) {
            List<String> messages = validations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
            throw new InputInvalidException(messages);
        }
    }
}
