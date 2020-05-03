package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.lib.error_handler.exception.ConflictException;
import com.github.saphyra.apphub.lib.error_handler.exception.RestException;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
class RegistrationRequestValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final UserDao userDao;

    void validate(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        String username = registrationRequest.getUsername();
        String password = registrationRequest.getPassword();

        validateEmail(email);
        validateUsername(username);
        validatePassword(password);
    }

    private void validateEmail(String email) {
        if (isNull(email)) {
            throw wrongPayloadException("email");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw wrongPayloadException("email", "invalid format");
        }

        if (userDao.findByEmail(email.toLowerCase()).isPresent()) {
            throw new ConflictException(new ErrorMessage(ErrorCode.EMAIL_ALREADY_EXISTS.name()), "Email already exists.");
        }
    }

    private void validateUsername(String username) {
        if (isNull(username)) {
            throw wrongPayloadException("username");
        }

        if (username.length() < 3) {
            throw invalidParamException(ErrorCode.USERNAME_TOO_SHORT, "Username too short.");
        }

        if (username.length() > 30) {
            throw invalidParamException(ErrorCode.USERNAME_TOO_LONG, "Username too long.");
        }

        if (userDao.findByUsername(username).isPresent()) {
            throw new ConflictException(new ErrorMessage(ErrorCode.USERNAME_ALREADY_EXISTS.name()), "Username already exists");
        }
    }

    private void validatePassword(String password) {
        if (isNull(password)) {
            throw wrongPayloadException("password");
        }

        if (password.length() < 6) {
            throw invalidParamException(ErrorCode.PASSWORD_TOO_SHORT, "Password is too short.");
        }

        if (password.length() > 30) {
            throw invalidParamException(ErrorCode.PASSWORD_TOO_LONG, "Password is too long.");
        }
    }

    private RestException wrongPayloadException(String fieldName) {
        return requireNonNull(wrongPayloadException(fieldName, "must not be null"));
    }

    private RestException wrongPayloadException(String fieldName, String message) {
        Map<String, String> params = new HashMap<>();
        params.put(fieldName, message);
        return new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), params), String.format("%s is null.", fieldName));
    }

    private RestException invalidParamException(ErrorCode errorCode, String logMessage) {
        return new BadRequestException(new ErrorMessage(errorCode.name()), logMessage);
    }
}
