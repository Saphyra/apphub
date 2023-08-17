package com.github.saphyra.integration.server.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidGenerator {
    public UUID create() {
        return UUID.randomUUID();
    }
}
