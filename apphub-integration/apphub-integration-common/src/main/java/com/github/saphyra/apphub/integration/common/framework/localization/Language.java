package com.github.saphyra.apphub.integration.common.framework.localization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {
    HUNGARIAN("hu"),
    ENGLISH("en");

    private final String locale;
}
