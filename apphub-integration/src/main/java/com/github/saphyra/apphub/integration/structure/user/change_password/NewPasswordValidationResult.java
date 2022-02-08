package com.github.saphyra.apphub.integration.structure.user.change_password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NewPasswordValidationResult {
    VALID(null),
    TOO_SHORT("Jelszó túl rövid (Minimum 6 karakter)."),
    TOO_LONG("Jelszó túl hosszú (Maximum 30 karakter).");

    private final String errorMessage;
}
