package com.github.saphyra.apphub.integration.framework;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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

        return Assertions.assertThat(item);
    }

    @SafeVarargs
    public static <T> void assertList(List<T> list, Consumer<T>... assertions) {
        List<Consumer<T>> assertionList = Arrays.stream(assertions)
            .toList();

        if (list.size() < assertionList.size()) {
            fail("More assertions provided (%s) than elements in list: %s".formatted(assertionList.size(), list.size()));
        }

        for (int i = 0; i < assertionList.size(); i++) {
            assertionList.get(i).accept(list.get(i));
        }
    }
}
