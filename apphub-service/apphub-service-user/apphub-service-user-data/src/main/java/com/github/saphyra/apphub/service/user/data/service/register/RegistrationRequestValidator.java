package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
//TODO proper exceptions
//TODO unit test
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
            throw new RuntimeException("Email is null.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Invalid email.");
        }

        if (userDao.findByEmail(email.toLowerCase()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
    }

    private void validateUsername(String username) {
        if (isNull(username)) {
            throw new RuntimeException("Username is null.");
        }

        if (username.length() < 3) {
            throw new RuntimeException("Username too short.");
        }

        if (username.length() > 30) {
            throw new RuntimeException("Username too long.");
        }

        if (userDao.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
    }

    private void validatePassword(String password) {
        if (isNull(password)) {
            throw new RuntimeException("Password is null.");
        }

        if (password.length() < 6) {
            throw new RuntimeException("Password too short.");
        }

        if (password.length() > 30) {
            throw new RuntimeException("Password too long.");
        }
    }
}
