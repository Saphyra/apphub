package com.github.saphyra.apphub.integration.frontend.model.registration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailValidationResult {
    VALID(null),
    INVALID("Érvénytelen e-mail cím.");

    private final String errorMessage;
}
