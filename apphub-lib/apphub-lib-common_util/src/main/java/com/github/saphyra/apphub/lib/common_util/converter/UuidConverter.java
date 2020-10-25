package com.github.saphyra.apphub.lib.common_util.converter;

import java.util.UUID;

public class UuidConverter extends ConverterBase<String, UUID> {
    @Override
    protected UUID processEntityConversion(String s) {
        return UUID.fromString(s);
    }

    @Override
    protected String processDomainConversion(UUID uuid) {
        return uuid.toString();
    }
}
