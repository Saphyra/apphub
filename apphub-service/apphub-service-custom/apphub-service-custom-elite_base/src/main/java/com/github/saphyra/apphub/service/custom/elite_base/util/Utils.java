package com.github.saphyra.apphub.service.custom.elite_base.util;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class Utils {
    //TODO unit test
    public static Double nullIfZero(double aDouble) {
        if (aDouble == 0) {
            return null;
        }

        return aDouble;
    }

    public static <T> T measuredOperation(Supplier<T> operation, String pattern) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        T result = operation.get();

        stopwatch.stop();
        log.info(pattern, stopwatch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }
}
