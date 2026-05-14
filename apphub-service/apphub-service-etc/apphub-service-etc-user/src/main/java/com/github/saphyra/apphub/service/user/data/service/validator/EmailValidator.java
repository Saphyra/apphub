package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9._-]+\\.[a-z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public void validateEmail(String email) {
        if (isNull(email)) {
            throw ExceptionFactory.invalidParam("email", "must not be null");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw ExceptionFactory.invalidParam("email", "invalid format");
        }
    }
}
