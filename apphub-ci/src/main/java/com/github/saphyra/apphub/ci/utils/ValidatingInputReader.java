package com.github.saphyra.apphub.ci.utils;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidatingInputReader {
    private final LocalizationService localizationService;

    public String getInput(LocalizationProvider localizationProvider, Function<String, Optional<LocalizationProvider>> validation) {
        return getInput(localizationProvider, Function.identity(), validation);
    }

    /**
     * Returns a validated input of a given type.
     *
     * @param localizationProvider The text to display before asking for user input.
     * @param mapper               Conversion between the text entered by the user, and the desired type
     * @param validation           Should return empty, if the input is valid. Should return error message if input is invalid.
     * @param <T>                  Return type
     * @return the validated, mapped input
     */
    public <T> T getInput(LocalizationProvider localizationProvider, Function<String, T> mapper, Function<T, Optional<LocalizationProvider>> validation) {
        while (true) {
            log.info("");
            localizationService.writeMessage(localizationProvider);

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            try {
                T result = mapper.apply(input);
                Optional<LocalizationProvider> validationResult = validation.apply(result);
                if (validationResult.isPresent()) {
                    localizationService.writeMessage(language -> LocalizedText.INVALID_PARAMETER.getLocalizedText(language).formatted(validationResult.get().getLocalizedText(language)));
                } else {
                    return result;
                }
            } catch (Exception e) {
                localizationService.writeMessage(LocalizedText.INVALID_PARAMETER);
            }
        }
    }
}
