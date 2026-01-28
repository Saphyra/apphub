package com.github.saphyra.apphub.lib.common_util;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Slf4j
@ToString(exclude = "supplier")
public class LazyLoadedField<T> {
    private final Supplier<T> supplier;
    private volatile T value;
    private volatile boolean loaded = false;

    private LazyLoadedField(T value) {
        this.supplier = null;
        this.value = value;
        this.loaded = true;
    }

    public static <T> LazyLoadedField<T> loaded(T value) {
        return new LazyLoadedField<>(value);
    }

    public static <T> LazyLoadedField<T> of(Supplier<T> supplier) {
        return new LazyLoadedField<>(supplier);
    }

    public synchronized T get() {
        if (loaded) {
            log.debug("Cached value is returned.");
            return value;
        }

        log.debug("Caching value...");
        value = supplier.get();
        loaded = true;

        return value;
    }
}
