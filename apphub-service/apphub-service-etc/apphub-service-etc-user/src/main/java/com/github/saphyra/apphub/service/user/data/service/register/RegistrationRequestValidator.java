package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.etc.user.model.account.RegistrationRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
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
    private final CommonConfigProperties commonConfigProperties;

    void validate(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        String username = registrationRequest.getUsername();
        String password = registrationRequest.getPassword();

        ValidationUtil.contains(registrationRequest.getLanguage(), commonConfigProperties.getSupportedLocales(), "language");

        emailValidator.validateEmail(email);
        usernameValidator.validateUsername(username);
        passwordValidator.validatePassword(password);
    }
}
