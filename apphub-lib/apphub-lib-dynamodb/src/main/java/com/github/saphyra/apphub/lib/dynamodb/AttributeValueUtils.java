package com.github.saphyra.apphub.lib.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

public class AttributeValueUtils {
    public static AttributeValue createString(String value) {
        if (isNull(value)) {
            return AttributeValue.builder()
                .nul(true)
                .build();
        }

        return AttributeValue.builder()
            .s(value)
            .build();
    }

    public static String getString(Map<String, AttributeValue> item, String column) {
        return Optional.ofNullable(item.get(column))
            .map(AttributeValue::s)
            .orElse(null);
    }
}
