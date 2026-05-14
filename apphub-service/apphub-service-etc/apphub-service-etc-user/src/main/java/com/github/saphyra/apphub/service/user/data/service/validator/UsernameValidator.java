package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class UsernameValidator {
    public void validateUsername(String username) {
        if (isNull(username)) {
            throw ExceptionFactory.invalidParam("username", "must not be null");
        }

        if (username.length() < 3) {
            throw ExceptionFactory.invalidParam("username", "too short");
        }

        if (username.length() > 30) {
            throw ExceptionFactory.invalidParam("username", "too long");
        }
    }
}
