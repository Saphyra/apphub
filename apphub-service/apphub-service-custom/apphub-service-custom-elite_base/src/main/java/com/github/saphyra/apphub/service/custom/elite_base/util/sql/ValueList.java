package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ValueList implements SegmentProvider{
    private final List<String> values;

    @Override
    public String get() {
        return values.stream()
            .map("'%s'"::formatted)
            .collect(Collectors.joining(" "));
    }
}
