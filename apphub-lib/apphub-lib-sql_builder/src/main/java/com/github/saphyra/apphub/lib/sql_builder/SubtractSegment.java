package com.github.saphyra.apphub.lib.sql_builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SubtractSegment implements SegmentProvider {
    private final List<SegmentProvider> numbers;

    public SubtractSegment(SegmentProvider... numbers) {
        this.numbers = Arrays.asList(numbers);
    }

    @Override
    public String get() {
        return numbers.stream()
            .map(SegmentProvider::get)
            .collect(Collectors.joining(" - "));
    }
}
