package com.github.saphyra.apphub.integration.core;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;

public class TestGroupValidator {
    private static final List<String> PLATFORM_GROUPS = ImmutableList.of("fe", "be");
    private static final List<String> FEATURE_GROUPS = ImmutableList.of(
        "account",
        "admin-panel",
        "calendar",
        "community",
        "index",
        "modules",
        "notebook",
        "skyxplore",
        "utils",
        "misc",
        "training",
        "villany-atesz"
    );

    public static void validateTestGroups(Method method) {
        List<String> groups = Arrays.stream(method.getAnnotation(Test.class).groups())
            .toList();

        if (PLATFORM_GROUPS.stream().noneMatch(groups::contains)) {
            fail(TestUtils.getMethodIdentifier(method) + " has no mandatory groups of " + PLATFORM_GROUPS);
        }

        if (FEATURE_GROUPS.stream().noneMatch(groups::contains)) {
            fail(TestUtils.getMethodIdentifier(method) + " has no mandatory groups of " + FEATURE_GROUPS);
        }
    }
}
