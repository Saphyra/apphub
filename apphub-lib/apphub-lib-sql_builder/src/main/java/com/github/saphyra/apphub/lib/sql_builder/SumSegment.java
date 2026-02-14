package com.github.saphyra.apphub.lib.sql_builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SumSegment implements SegmentProvider {
    private final List<SegmentProvider> values;

    public SumSegment(SegmentProvider... values) {
        this.values = Arrays.asList(values);
    }

    @Override
    public String get() {
        return values.stream()
            .map(SegmentProvider::get)
            .collect(Collectors.joining(" + "));
    }
}
