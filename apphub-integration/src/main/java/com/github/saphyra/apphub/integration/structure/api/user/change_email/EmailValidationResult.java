package com.github.saphyra.apphub.integration.structure.api.user.change_email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailValidationResult {
    VALID(null),
    BLANK("Must not be blank."),
    INVALID("Invalid e-mail.");

    private final String errorMessage;
}
