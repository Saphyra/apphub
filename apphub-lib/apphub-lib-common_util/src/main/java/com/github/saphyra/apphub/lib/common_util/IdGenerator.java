package com.github.saphyra.apphub.lib.common_util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGenerator {
    public String generateRandomId() {
        return randomUUID().toString();
    }

    public UUID randomUUID() {
        return UUID.randomUUID();
    }
}
