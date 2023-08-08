package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum CitizenOrder {
    ASCENDING("1"),
    DESCENDING("-1");

    private final String value;

    public List<String> sort(List<String> in) {
        return in.stream()
            .sorted((o1, o2) -> Integer.parseInt(value) * o1.compareTo(o2))
            .collect(Collectors.toList());
    }
}
