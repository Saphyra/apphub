package com.github.saphyra.apphub.lib.common_util.converter;

import java.util.Optional;
import java.util.function.Function;

public class NullSafeConverter {
    public static  <T, R> R safeConvert(T in, Function<T, R> mapper) {
        return Optional.ofNullable(in)
            .map(mapper)
            .orElse(null);
    }
}
