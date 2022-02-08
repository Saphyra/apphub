package com.github.saphyra.apphub.integration.structure.user.registration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PasswordValidationResult {
    VALID(null),
    TOO_SHORT("Jelszó túl rövid (Minimum 6 karakter)."),
    TOO_LONG("Jelszó túl hosszú (Maximum 30 karakter)."),
    INVALID_CONFIRM_PASSWORD("A jelszavak nem egyeznek.");

    private final String errorMessage;
}
