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
