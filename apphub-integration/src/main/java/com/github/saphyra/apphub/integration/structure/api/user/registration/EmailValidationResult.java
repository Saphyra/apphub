package com.github.saphyra.apphub.integration.structure.api.user.registration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailValidationResult {
    VALID(null),
    INVALID("Érvénytelen e-mail.");

    private final String errorMessage;
}
