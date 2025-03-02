package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ListValue implements SegmentProvider {
    private final List<? extends SegmentProvider> values;

    public ListValue(SegmentProvider segmentProvider) {
        this.values = List.of(segmentProvider);
    }

    @Override
    public String get() {

        String content = values.stream()
            .map(SegmentProvider::get)
            .collect(Collectors.joining(", "));

        return "(%s)".formatted(content);
    }
}
