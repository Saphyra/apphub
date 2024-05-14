package com.github.saphyra.apphub.ci.value;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Command {
    ;

    private final String command;

    public String assemble(String key, Object value) {
        return command.replace("${%s}".formatted(key), value.toString());
    }
}
