package com.github.saphyra.apphub.lib.common_util.cache;

import com.google.common.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public abstract class AbstractCache<K, T> {
    @NonNull
    protected Cache<K, Optional<T>> cache;

    protected abstract Optional<T> load(K key);

    public Optional<T> get(K key) {
        try {
            return cache.get(key, () -> load(key));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> getIfPresent(K key) {
        Optional<T> result = cache.getIfPresent(key);

        if (isNull(result)) {
            return Optional.empty();
        }

        return result;
    }

    public void invalidate(K key) {
        cache.invalidate(key);
    }

    public void clear() {
        cache.invalidateAll();
    }

    public void put(K key, T value) {
        cache.put(key, Optional.ofNullable(value));
    }
}
