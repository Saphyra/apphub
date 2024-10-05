package com.github.saphyra.apphub.integration.core.driver;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.google.common.base.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WebDriverMode {
    //True == Headless
    HEADLESS(() -> true),
    HEADED(() -> false),
    DEFAULT(() -> TestConfiguration.WEB_DRIVER_HEADLESS_MODE),
    ;

    private final Supplier<Boolean> mode;
}
