package com.github.saphyra.apphub.integration.structure.api.user.change_password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChPasswordPasswordValidationResult {
    VALID(null),
    EMPTY_PASSWORD("Must not be blank.");

    private final String errorMessage;
}
