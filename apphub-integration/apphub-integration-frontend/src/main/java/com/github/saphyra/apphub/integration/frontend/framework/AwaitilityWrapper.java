package com.github.saphyra.apphub.integration.frontend.framework;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class AwaitilityWrapper {
    @NonNull
    private final ConditionFactory conditionFactory;

    public static AwaitilityWrapper createDefault() {
        ConditionFactory conditionFactory = await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS);
        return wrap(conditionFactory);
    }

    public static AwaitilityWrapper wrap(ConditionFactory conditionFactory) {
        return new AwaitilityWrapper(conditionFactory);
    }

    public boolean until(Callable<Boolean> callable) {
        try {
            conditionFactory.until(callable);
            return true;
        } catch (ConditionTimeoutException e) {
            log.info(e.getMessage());
            return false;
        }
    }
}
