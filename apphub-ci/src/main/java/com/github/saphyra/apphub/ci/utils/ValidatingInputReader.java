package com.github.saphyra.apphub.ci.utils;

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
    public String getInput(String label, Function<String, Optional<String>> validation) {
        return getInput(label, Function.identity(), validation);
    }

    /**
     * Returns a validated input of a given type.
     *
     * @param label      The text to display before asking for user input.
     * @param mapper     Conversion between the text entered by the user, and the desired type
     * @param validation Should return empty, if the input is valid. Should return error message if input is invalid.
     * @return  the validated, mapped input
     * @param <T> Return type
     */
    public <T> T getInput(String label, Function<String, T> mapper, Function<T, Optional<String>> validation) {
        while (true) {
            log.info("");
            log.info(label);

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            try {
                T result = mapper.apply(input);
                Optional<String> validationResult = validation.apply(result);
                if (validationResult.isPresent()) {
                    log.info("Invalid parameter: {}", validationResult.get()); //TODO translate
                } else {
                    return result;
                }
            } catch (Exception e) {
                log.info("Invalid parameter."); //TODO translate
            }
        }
    }
}
