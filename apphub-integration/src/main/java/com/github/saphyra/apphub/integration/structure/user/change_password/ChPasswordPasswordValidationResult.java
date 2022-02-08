package com.github.saphyra.apphub.integration.structure.user.change_password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChPasswordPasswordValidationResult {
    VALID(null),
    EMPTY_PASSWORD("A jelszó megadása kötelező.");

    private final String errorMessage;
}
