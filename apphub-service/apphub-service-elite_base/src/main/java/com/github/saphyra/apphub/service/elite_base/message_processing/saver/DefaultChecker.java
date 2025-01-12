package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

@AllArgsConstructor
class DefaultChecker implements Checker {
    private final Object newValue;
    private final Supplier<Object> currentValueProvider;

    @Override
    public boolean check() {
        if (isNull(newValue)) {
            return true;
        }

        if (newValue instanceof Object[]) {
            return Arrays.equals((Object[]) newValue, (Object[]) currentValueProvider.get());
        }

        return Objects.equals(newValue, currentValueProvider.get());
    }
}
