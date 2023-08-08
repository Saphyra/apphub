package com.github.saphyra.apphub.integration.structure.api.user.change_username;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UsernameValidationResult {
    VALID(null),
    TOO_SHORT("Felhasználónév túl rövid (Minimum 3 karakter)."),
    TOO_LONG("Felhasználónév túl hosszú (Maximum 30 karakter).");

    private final String errorMessage;
}
