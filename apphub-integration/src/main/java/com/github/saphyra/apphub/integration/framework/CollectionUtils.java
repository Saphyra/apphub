package com.github.saphyra.apphub.integration.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtils {
    @SafeVarargs
    public static <T> Set<T> toSet(T... elements) {
        Set<T> result = new HashSet<>();
        Collections.addAll(result, elements);
        return result;
    }

    @SafeVarargs
    public static <T> List<T> toList(T... elements) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, elements);
        return list;
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
