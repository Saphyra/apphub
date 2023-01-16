package com.github.saphyra.apphub.integration.framework;

import java.util.UUID;

public class IdGenerator {
    public String generateRandomId() {
        return randomUuid().toString();
    }

    public UUID randomUuid() {
        return UUID.randomUUID();
    }
}
