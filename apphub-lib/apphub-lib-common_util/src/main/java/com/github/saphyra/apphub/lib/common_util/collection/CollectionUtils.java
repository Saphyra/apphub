package com.github.saphyra.apphub.lib.common_util.collection;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return toList(list, elements);
    }

    @SafeVarargs
    public static <L extends List<T>, T> L toList(L list, T... elements) {
        Collections.addAll(list, elements);
        return list;
    }

    public static <K, V> Map<K, V> singleValueMap(K key, V value) {
        return singleValueMap(key, value, new HashMap<>());
    }

    public static <K, V, M extends Map<K, V>> M singleValueMap(K key, V value, M map) {
        map.put(key, value);
        return map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> toMap(BiWrapper<K, V>... entries) {
        return toMap(new HashMap<>(), entries);
    }

    @SafeVarargs
    public static <T extends Map<K, V>, K, V> T toMap(T instance, BiWrapper<K, V>... entries) {
        for (BiWrapper<K, V> entry : entries) {
            instance.put(entry.getEntity1(), entry.getEntity2());
        }

        return instance;
    }

    @SafeVarargs
    public static <K, V, T> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper, T... items) {
        return toMap(keyMapper, valueMapper, Arrays.asList(items));
    }

    public static <K, V, T> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper, Collection<T> items) {
        Map<K, V> result = new HashMap<>();
        items.forEach(t -> result.put(keyMapper.apply(t), valueMapper.apply(t)));
        return result;
    }

    public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, Collection<V>> in) {
        MultiValueMap<K, V> result = new LinkedMultiValueMap<>();
        in.forEach((k, vs) -> result.put(k, new ArrayList<>(vs)));
        return result;
    }

    @SafeVarargs
    public static <T> List<T> asList(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    public static <K, V, R> OptionalMap<K, V> mapToOptionalMap(List<R> list, Function<R, K> keyMapper, Function<R, V> valueMapper) {
        Map<K, V> map = list.stream()
            .collect(Collectors.toMap(keyMapper, valueMapper));

        return new OptionalHashMap<>(map);
    }
}
