package com.github.saphyra.apphub.test.common;

import org.assertj.core.api.ObjectAssert;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class CustomAssertions {
    public static <T> ObjectAssert<T> singleListAssertThat(List<T> singleValueList) {
        if (singleValueList.isEmpty()) {
            fail("List is empty.");
        }

        if (singleValueList.size() > 1) {
            fail("List has multiple elements");
        }

        T item = singleValueList.get(0);

        return assertThat(item);
    }

    public static <T> ObjectAssert<T> optionalAssertThat(Optional<T> optional) {
        assertThat(optional).isNotEmpty();

        return assertThat(optional.get());
    }

    public static <K, V> ObjectAssert<V> singleMapAssert(Map<K, V> map, K key) {
        assertThat(map).containsKey(key);

        return assertThat(map.get(key));
    }
}
