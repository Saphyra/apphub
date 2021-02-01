package com.github.saphyra.apphub.lib.common_util.collection;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtils {
    public static <K, V> Map<K, V> singleValueMap(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
