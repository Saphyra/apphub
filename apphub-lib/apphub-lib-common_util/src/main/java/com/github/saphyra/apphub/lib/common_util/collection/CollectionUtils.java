package com.github.saphyra.apphub.lib.common_util.collection;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollectionUtils {
    public static <T> Set<T> toSet(T... elements) {
        Set<T> result = new HashSet<>();
        Collections.addAll(result, elements);
        return result;
    }

    public static <K, V> Map<K, V> singleValueMap(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> toMap(BiWrapper<K, V>... entries) {
        Map<K, V> result = new HashMap<>();
        for (BiWrapper<K, V> entry : entries) {
            result.put(entry.getEntity1(), entry.getEntity2());
        }

        return result;
    }
}
