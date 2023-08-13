package com.github.saphyra.apphub.integration.structure.api.user.change_password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConfirmPasswordValidationResult {
    VALID(null),
    INVALID_CONFIRM_PASSWORD("Incorrect confirm password.");

    private final String errorMessage;
}
