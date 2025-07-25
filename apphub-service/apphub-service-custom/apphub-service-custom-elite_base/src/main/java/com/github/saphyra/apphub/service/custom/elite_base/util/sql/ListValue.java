package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ListValue implements SegmentProvider {
    private final List<? extends SegmentProvider> values;

    public ListValue(SegmentProvider segmentProvider) {
        this.values = List.of(segmentProvider);
    }

    public ListValue(Collection<?> items) {
        values = items.stream()
            .map(o -> new WrappedValue(o.toString()))
            .toList();
    }

    @Override
    public String get() {

        String content = values.stream()
            .map(SegmentProvider::get)
            .collect(Collectors.joining(", "));

        return "(%s)".formatted(content);
    }
}
