package com.github.saphyra.apphub.integration.frontend.model.account.change_password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConfirmPasswordValidationResult {
    VALID(null),
    INVALID_CONFIRM_PASSWORD("A jelszavak nem egyeznek.");

    private final String errorMessage;
}
