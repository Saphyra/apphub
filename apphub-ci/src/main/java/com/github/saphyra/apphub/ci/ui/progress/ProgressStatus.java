package com.github.saphyra.apphub.ci.ui.progress;

import java.util.Arrays;

enum ProgressStatus {
    WAITING,
    IN_PROGRESS,
    COMPLETED,
    FAILED;

    static int maxLength() {
        return Arrays.stream(values())
            .mapToInt(status -> status.name().length())
            .max()
            .orElse(0);
    }
}
