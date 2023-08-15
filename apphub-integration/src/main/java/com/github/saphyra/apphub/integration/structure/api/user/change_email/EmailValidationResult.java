package com.github.saphyra.apphub.integration.structure.api.user.change_email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailValidationResult {
    VALID(null),
    INVALID("Invalid e-mail address.");

    private final String errorMessage;
}
