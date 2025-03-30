package com.github.saphyra.integration.server.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGenerator {
    public String generateRandomId() {
        return randomUuid().toString();
    }

    public UUID randomUuid() {
        return UUID.randomUUID();
    }
}
