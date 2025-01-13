package com.github.saphyra.apphub.lib.common_util.converter;

import java.util.UUID;

import static java.util.Objects.isNull;

public class UuidConverter extends ConverterBase<String, UUID> {
    @Override
    protected UUID processEntityConversion(String s) {
        if (isNull(s)) {
            return null;
        }

        return UUID.fromString(s);
    }

    @Override
    protected String processDomainConversion(UUID uuid) {
        if (isNull(uuid)) {
            return null;
        }

        return uuid.toString();
    }
}
