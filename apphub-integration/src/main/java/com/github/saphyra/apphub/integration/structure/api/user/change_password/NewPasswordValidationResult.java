package com.github.saphyra.apphub.integration.structure.api.user.change_password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NewPasswordValidationResult {
    VALID(null),
    TOO_SHORT("Password too short. (Minimum 6 characters)"),
    TOO_LONG("Password too long. (Maximum 30 characters)");

    private final String errorMessage;
}
