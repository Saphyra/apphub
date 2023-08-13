package com.github.saphyra.apphub.integration.structure.api.user.delete_account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeleteAccountPasswordValidationResult {
    VALID(null),
    EMPTY_PASSWORD("Password is empty.");

    private final String errorMessage;
}
