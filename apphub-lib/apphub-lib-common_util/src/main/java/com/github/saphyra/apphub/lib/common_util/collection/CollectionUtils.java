package com.github.saphyra.apphub.lib.common_util.collection;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtils {
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
