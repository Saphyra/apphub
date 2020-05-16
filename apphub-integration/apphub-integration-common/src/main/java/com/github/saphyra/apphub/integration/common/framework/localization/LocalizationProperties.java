package com.github.saphyra.apphub.integration.common.framework.localization;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@UtilityClass
public class LocalizationProperties {
    private final Map<String, Properties> PROPERTIES_MAP = new HashMap<>();

    static {
        Stream.of(Language.values())
            .map(Language::getLocale)
            .forEach(locale -> {
                Properties properties = new Properties();
                String fileName = String.format("localization/%s.properties", locale);
                InputStream inputStream = LocalizationProperties.class.getClassLoader().getResourceAsStream(fileName);
                if (isNull(inputStream)) {
                    throw new IllegalStateException(String.format("File not found: %s", fileName));
                }

                try {
                    properties.load(inputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                PROPERTIES_MAP.put(locale, properties);
            });
    }

    public String getProperty(Language locale, LocalizationKey key) {
        return Optional.ofNullable(PROPERTIES_MAP.get(locale.getLocale()))
            .flatMap(properties -> Optional.ofNullable(properties.getProperty(key.name())))
            .orElseThrow(() -> new IllegalStateException(String.format("Localization not found for locale %s and key %s", locale, key)));
    }
}
