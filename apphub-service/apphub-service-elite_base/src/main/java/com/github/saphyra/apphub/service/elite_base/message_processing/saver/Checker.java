package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

@AllArgsConstructor
//TODO unit test
class Checker {
    private final Object newValue;
    private final Supplier<Object> currentValueProvider;

    /**
     * @return true, if parameters match. False if update is needed.
     */
    boolean check() {
        if (isNull(newValue)) {
            return true;
        }

        if (newValue instanceof Object[]) {
            return Arrays.equals((Object[]) newValue, (Object[]) currentValueProvider.get());
        }

        return Objects.equals(newValue, currentValueProvider.get());
    }
}
