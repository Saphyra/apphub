package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

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
