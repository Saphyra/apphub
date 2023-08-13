package com.github.saphyra.apphub.integration.structure.api.user.registration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PasswordValidationResult {
    VALID(null),
    TOO_SHORT("Password too short (Minimum 6 characters)."),
    TOO_LONG("Passwrod too long (Maximum 30 characters)."),
    INVALID_CONFIRM_PASSWORD("Incorrect confirm password.");

    private final String errorMessage;
}
