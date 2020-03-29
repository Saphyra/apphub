package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.converter.ConverterBase;

import java.util.UUID;

//TODO unit test
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
