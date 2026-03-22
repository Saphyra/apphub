package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class PasswordValidator {
    public void validatePassword(String password) {
        validatePassword(password, "password");
    }

    public void validatePassword(String password, String fieldName) {
        if (isNull(password)) {
            throw ExceptionFactory.invalidParam(fieldName, "must not be null");
        }

        if (password.length() < 6) {
            throw ExceptionFactory.invalidParam("password", "too short");
        }

        if (password.length() > 30) {
            throw ExceptionFactory.invalidParam("password", "too long");
        }
    }
}
