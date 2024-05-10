package com.github.saphyra.apphub.integration.framework;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;

import java.util.List;

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
}
