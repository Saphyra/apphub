package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.framework.concurrent.ObjectWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.ObjectAssert;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
        return create(5, TimeUnit.SECONDS, 250, TimeUnit.MILLISECONDS);
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

    public static <T> Optional<T> getOptionalWithWait(Supplier<Optional<T>> supplier) {
        return getOptionalWithWait(supplier, Optional::isPresent);
    }

    public static <T> Optional<T> getOptionalWithWait(Supplier<Optional<T>> supplier, Predicate<Optional<T>> predicate) {
        GetWithWaitHelper<Optional<T>> helper = new GetWithWaitHelper<>(supplier, predicate);
        createDefault()
            .until(helper::get);

        return helper.getResult(t -> Optional.ofNullable(t).map(Optional::get));
    }

    public static <T> T getSingleItemFromListWithWait(Supplier<List<T>> supplier) {
        List<T> result = getListWithWait(supplier, list -> !list.isEmpty());

        if (result.size() != 1) {
            throw new RuntimeException("Expected list size exceeded.");
        }

        return result.get(0);
    }

    public static <T> List<T> getListWithWait(Supplier<List<T>> supplier, Predicate<List<T>> predicate) {
        GetWithWaitHelper<List<T>> helper = new GetWithWaitHelper<>(supplier, predicate);

        createDefault()
            .until(helper::get);

        return helper.getResult(result -> Optional.ofNullable(result).orElseThrow(() -> new RuntimeException("Expected list not found.")));
    }

    public static <T> Optional<T> getWithWait(Supplier<T> supplier, Predicate<T> predicate, int timeoutSeconds, int pollInterval) {
        return getWithWait(supplier, predicate, timeoutSeconds, TimeUnit.SECONDS, pollInterval, TimeUnit.SECONDS);
    }

    public static <T> Optional<T> getWithWait(Supplier<T> supplier, Predicate<T> predicate, int timeout, TimeUnit timeoutUnit, int pollInterval, TimeUnit pollUnit) {
        GetWithWaitHelper<T> helper = new GetWithWaitHelper<>(supplier, predicate);
        create(timeout, timeoutUnit, pollInterval, pollUnit)
            .until(helper::get);

        return helper.getResult(Optional::ofNullable);
    }

    public static <T> ObjectAssert<T> assertWithWaitList(Supplier<List<T>> supplier) {
        return assertWithWaitList(supplier, list -> list.size() == 1, list -> list.get(0));
    }

    public static <T> ObjectAssert<T> assertWithWaitList(Supplier<List<T>> supplier, Predicate<List<T>> predicate, Function<List<T>, T> selector) {
        List<T> list = getListWithWait(supplier, predicate);

        T item = selector.apply(list);

        return assertThat(item);
    }

    public static void awaitAssert(Runnable assertions) {
        createDefault()
            .until(() -> {
                assertions.run();
                return true;
            })
            .assertTrue("Assertions failed.");
    }

    public static <T> void awaitAssert(Supplier<T> supplier, Consumer<T> assertions) {
        createDefault()
            .until(() -> {
                assertions.accept(supplier.get());
                return true;
            })
            .assertTrue("Assertions failed.");
    }

    public AwaitResult until(Callable<Boolean> callable) {
        ObjectWrapper<Throwable> exception = new ObjectWrapper<>();

        try {
            conditionFactory.until(() -> {
                try {
                    return callable.call();
                } catch (Throwable e) {
                    exception.setValue(e);
                    return false;
                }
            });
            return new AwaitResult(true, null);
        } catch (ConditionTimeoutException e) {
            return new AwaitResult(false, exception.getValue());
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class AwaitResult {
        private final boolean result;
        private final Throwable cause;

        public void assertTrue() {
            assertThat(result).isTrue();
        }

        public void assertTrue(String message) {
            if (!result) {
                throw new IllegalStateException(message, cause);
            }
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
