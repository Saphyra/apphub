package com.github.saphyra.apphub.ci.value;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LocalRunMode {
    DEFAULT(LocalizedText.DEFAULT),
    SKIP_BUILD(LocalizedText.LOCAL_RUN_MODE_SKIP_BUILD),
    SKIP_TESTS(LocalizedText.LOCAL_RUN_MODE_SKIP_TESTS);

    private final LocalizedText localization;
}
