package com.github.saphyra.apphub.integration.frontend.framework;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
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

    public static <T> Optional<T> getWithWait(Supplier<T> supplier, Predicate<T> predicate) {
        GetWithWaitHelper<T> helper = new GetWithWaitHelper<>(supplier, predicate);
        createDefault()
            .until(helper::get);

        return helper.getResult(Optional::ofNullable);
    }
    
    public static <T>List<T> getListWithWait(Supplier<List<T>> supplier, Predicate<List<T>> predicate){
        GetWithWaitHelper<List<T>> helper = new GetWithWaitHelper<>(supplier, predicate);
        
        createDefault()
            .until(helper::get);
        
        return helper.getResult(result -> Optional.ofNullable(result).orElse(Collections.emptyList()));
    }

    public AwaitResult until(Callable<Boolean> callable) {
        try {
            conditionFactory.until(callable);
            return new AwaitResult(true);
        } catch (ConditionTimeoutException e) {
            log.info("Condition failed.", e);
            return new AwaitResult(false);
        }
    }

    @RequiredArgsConstructor
    public static class AwaitResult{
        private final boolean result;

        public void assertTrue() {
            assertThat(result).isTrue();
        }
    }

    @RequiredArgsConstructor
    private static class GetWithWaitHelper<T> {
        private final Supplier<T> supplier;
        private final Predicate<T> predicate;

        private T result;

        public boolean get() {
            T result = supplier.get();
            boolean positive = predicate.test(result);
            log.info("Is test positive: {}", positive);
            if (positive) {
                this.result = result;
            }
            return positive;
        }

        public <R> R getResult(Function<T, R> mapper) {
            return mapper.apply(result);
        }
    }
}
