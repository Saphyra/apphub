package com.github.saphyra.apphub.ci.ui.startup;

import java.util.Arrays;

enum StartupStatus {
    WAITING,
    IN_PROGRESS,
    STARTED;

    static int maxLength() {
        return Arrays.stream(values())
            .mapToInt(status -> status.name().length())
            .max()
            .orElse(0);
    }
}
