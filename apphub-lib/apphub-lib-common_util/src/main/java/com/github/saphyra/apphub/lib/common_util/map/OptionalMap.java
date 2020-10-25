package com.github.saphyra.apphub.lib.common_util.map;

import java.util.Map;
import java.util.Optional;

public interface OptionalMap<K, V> extends Map<K, V> {
    default Optional<V> getOptional(K key) {
        return Optional.ofNullable(get(key));
    }
}
