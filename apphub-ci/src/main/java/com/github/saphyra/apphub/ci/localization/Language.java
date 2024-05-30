package com.github.saphyra.apphub.ci.localization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {
    ENGLISH(LocalizedText.ENGLISH),
    HUNGARIAN(LocalizedText.HUNGARIAN),
    ;

    private final LocalizedText localization;
}
