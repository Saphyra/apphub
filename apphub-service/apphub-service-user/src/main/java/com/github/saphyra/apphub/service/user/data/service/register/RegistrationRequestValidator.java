package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.service.validator.EmailValidator;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import com.github.saphyra.apphub.service.user.data.service.validator.UsernameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RegistrationRequestValidator {
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final UsernameValidator usernameValidator;

    void validate(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        String username = registrationRequest.getUsername();
        String password = registrationRequest.getPassword();

        emailValidator.validateEmail(email);
        usernameValidator.validateUsername(username);
        passwordValidator.validatePassword(password);
    }
}
