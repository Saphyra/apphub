package com.github.saphyra.apphub.integration.structure.api.user.change_username;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UsernameValidationResult {
    VALID(null),
    TOO_SHORT("Username too short. (Minimum 3 characters)"),
    TOO_LONG("Username too long. (Maximum 30 characters)");

    private final String errorMessage;
}
