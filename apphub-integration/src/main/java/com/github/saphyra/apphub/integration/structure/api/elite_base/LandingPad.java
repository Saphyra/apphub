package com.github.saphyra.apphub.integration.structure.api.elite_base;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LandingPad {
    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int size;

    public boolean isLargeEnough(LandingPad minLandingPad) {
        return size >= minLandingPad.size;
    }
}
