package com.github.saphyra.apphub.integration.frontend.model.account.delete_account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeleteAccountPasswordValidationResult {
    VALID(null),
    EMPTY_PASSWORD("A jelszó megadása kötelező.");

    private final String errorMessage;
}
