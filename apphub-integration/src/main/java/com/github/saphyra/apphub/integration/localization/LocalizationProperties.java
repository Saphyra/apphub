package com.github.saphyra.apphub.integration.localization;

import com.github.saphyra.apphub.integration.TestBase;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@UtilityClass
public class LocalizationProperties {
    private final Map<String, Map<String, String>> PROPERTIES_MAP = new HashMap<>();

    static {
        Stream.of(Language.values())
            .map(Language::getLocale)
            .forEach(locale -> {
                String fileName = String.format("localization/%s.json", locale);
                InputStream inputStream = LocalizationProperties.class.getClassLoader().getResourceAsStream(fileName);
                if (isNull(inputStream)) {
                    throw new IllegalStateException(String.format("File not found: %s", fileName));
                }

                TypeReference<Map<String, String>> ref = new TypeReference<Map<String, String>>() {
                };
                Map<String, String> propertyMap = TestBase.OBJECT_MAPPER_WRAPPER.readValue(inputStream, ref);

                PROPERTIES_MAP.put(locale, propertyMap);
            });
    }

    public String getProperty(Language locale, LocalizationKey key) {
        return Optional.ofNullable(PROPERTIES_MAP.get(locale.getLocale()))
            .flatMap(properties -> Optional.ofNullable(properties.get(key.name())))
            .orElseThrow(() -> new IllegalStateException(String.format("Localization not found for locale %s and key %s", locale, key)));
    }
}
