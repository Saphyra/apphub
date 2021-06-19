package com.github.saphyra.apphub.integration.common.framework;

import com.github.saphyra.apphub.integration.common.TestBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class AwaitilityWrapper {
    @NonNull
    private final ConditionFactory conditionFactory;

    public static AwaitilityWrapper createDefault() {
        return create(10, 1);
    }

    public static AwaitilityWrapper create(int timeout, int pollInterval) {
        return create(timeout, TimeUnit.SECONDS, pollInterval, TimeUnit.SECONDS);
    }

    private static AwaitilityWrapper create(int timeout, TimeUnit timeoutUnit, int pollInterval, TimeUnit pollUnit) {
        ConditionFactory conditionFactory = Awaitility.await()
            .atMost(timeout, timeoutUnit)
            .pollInterval(pollInterval, pollUnit);
        return wrap(conditionFactory);
    }

    public static AwaitilityWrapper wrap(ConditionFactory conditionFactory) {
        return new AwaitilityWrapper(conditionFactory);
    }

    public static <T> Optional<T> findWithWait(Supplier<List<T>> supplier, Predicate<T> predicate) {
        FindWithWaitHelper<T> helper = new FindWithWaitHelper<>(supplier, predicate);
        createDefault()
            .until(helper::search);

        return helper.getResult();
    }

    public static <T> Optional<T> findWithWait(int timeout, Supplier<List<T>> supplier, Predicate<T> predicate) {
        FindWithWaitHelper<T> helper = new FindWithWaitHelper<>(supplier, predicate);
        create(timeout, 1)
            .until(helper::search);

        return helper.getResult();
    }

    public static <T> Optional<T> getWithWait(Supplier<T> supplier, Predicate<T> predicate) {
        GetWithWaitHelper<T> helper = new GetWithWaitHelper<>(supplier, predicate);
        createDefault()
            .until(helper::get);

        return helper.getResult(Optional::ofNullable);
    }

    public static <T> List<T> getListWithWait(Supplier<List<T>> supplier, Predicate<List<T>> predicate) {
        GetWithWaitHelper<List<T>> helper = new GetWithWaitHelper<>(supplier, predicate);

        createDefault()
            .until(helper::get);

        return helper.getResult(result -> Optional.ofNullable(result).orElseThrow(() -> new RuntimeException("Expected list not found.")));
    }

    public static <T> Optional<T> getWithWait(Supplier<T> supplier, Predicate<T> predicate, int timeout, TimeUnit timeoutUnit, int pollInterval, TimeUnit pollUnit) {
        GetWithWaitHelper<T> helper = new GetWithWaitHelper<>(supplier, predicate);
        create(timeout, timeoutUnit, pollInterval, pollUnit)
            .until(helper::get);

        return helper.getResult(Optional::ofNullable);
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
    public static class AwaitResult {
        private final boolean result;

        public void assertTrue() {
            assertThat(result).isTrue();
        }

        public void assertTrue(String message) {
            if (!result) {
                throw new IllegalStateException(message);
            }
        }

        public void softAssertTrue() {
            TestBase.getSoftAssertions().assertThat(result).isTrue();
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
            log.debug("Is test positive: {}", positive);
            if (positive) {
                this.result = result;
            }
            return positive;
        }

        public <R> R getResult(Function<T, R> mapper) {
            return mapper.apply(result);
        }
    }

    @RequiredArgsConstructor
    private static class FindWithWaitHelper<T> {
        private final Supplier<List<T>> supplier;
        private final Predicate<T> predicate;

        @Getter
        private Optional<T> result = Optional.empty();

        public boolean search() {
            result = supplier.get()
                .stream()
                .filter(predicate)
                .findFirst();
            return result.isPresent();
        }
    }
}
