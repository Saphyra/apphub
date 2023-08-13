package com.github.saphyra.apphub.integration.structure.api.user.change_email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChEmailPasswordValidationResult {
    VALID(null),
    EMPTY_PASSWORD("Password is empty.");

    private final String errorMessage;
}
