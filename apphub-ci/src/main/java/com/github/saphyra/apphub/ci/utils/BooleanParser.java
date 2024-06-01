package com.github.saphyra.apphub.ci.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BooleanParser {
    private static final List<String> YES_OPTIONS = List.of(
        "yes",
        "y",
        "igen",
        "i"
    );

    public Boolean parse(String string) {
        return YES_OPTIONS.contains(string.toLowerCase());
    }
}
