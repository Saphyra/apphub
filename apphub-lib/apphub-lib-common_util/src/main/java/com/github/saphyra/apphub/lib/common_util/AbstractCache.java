package com.github.saphyra.apphub.lib.common_util;

import com.google.common.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

    public void invalidate(K key) {
        cache.invalidate(key);
    }

    public void clear() {
        cache.invalidateAll();
    }
}
