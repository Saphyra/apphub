package com.github.saphyra.apphub.integration.structure.api.user.change_username;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UsernameValidationResult {
    VALID(null),
    TOO_SHORT("Username too short (3 character min.)."),
    TOO_LONG("Username too long (30 characters max.).");

    private final String errorMessage;
}
