package com.github.saphyra.apphub.integration.structure.api.villany_atesz;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ToolStatus {
    DEFAULT("Default"),
    SCRAPPED("Scrapped"),
    LOST("Lost"),
    DAMAGED("Damaged");

    private final String label;

    public static ToolStatus fromLabel(String label) {
        return Arrays.stream(values())
            .filter(toolStatus -> toolStatus.label.equals(label))
            .findAny()
            .orElseThrow(() -> new RuntimeException("ToolStatus not found by label " + label));
    }
}
