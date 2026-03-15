package com.github.saphyra.apphub.integration.core.feature_lock;

import lombok.Getter;

public enum Feature {
    ROLE_TEST,
    ;

    @Getter
    private final int permits;

    Feature() {
        this.permits = 1;
    }

    Feature(int permits) {
        this.permits = permits;
    }
}
