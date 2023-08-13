package com.github.saphyra.apphub.integration.structure.api.user.change_password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NewPasswordValidationResult {
    VALID(null),
    TOO_SHORT("Password too short (6 characters min.)."),
    TOO_LONG("Password too long (30 characters max.).");

    private final String errorMessage;
}
