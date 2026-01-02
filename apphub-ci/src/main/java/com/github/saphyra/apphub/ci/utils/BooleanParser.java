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
    private static final List<String> NO_OPTIONS = List.of(
        "no",
        "n",
        "nem"
    );

    public Boolean parse(String string) {
        String s = string.toLowerCase();

        if(YES_OPTIONS.contains(s)){
            return true;
        }

        if(NO_OPTIONS.contains(s)){
            return false;
        }

        return null;
    }
}
