package com.github.saphyra.apphub.integration.localization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum Language {
    HUNGARIAN("hu"),
    ENGLISH("en");

    private final String locale;

    public static Language fromClasses(List<String> classes) {
        return Arrays.stream(values())
            .filter(language -> classes.contains(language.locale))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("ClassList " + classes + " cannot be mapped to Language."));
    }
}
