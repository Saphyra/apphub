package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ServiceListValidator implements Function<String[], Optional<LocalizationProvider>> {
    private final Services services;

    @Override
    public Optional<LocalizationProvider> apply(String[] input) {
        if (input.length == 0) {
            return Optional.empty();
        }

        return Arrays.stream(input)
            .filter(serviceName -> services.getServices().stream().noneMatch(service -> service.getName().equals(serviceName)))
            .findAny()
            .map(serviceName -> language -> LocalizedText.SERVICE_NOT_FOUND.getLocalizedText(language).formatted(serviceName));
    }
}
