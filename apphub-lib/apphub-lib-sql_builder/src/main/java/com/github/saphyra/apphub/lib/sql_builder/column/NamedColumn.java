package com.github.saphyra.apphub.lib.sql_builder.column;

import com.github.saphyra.apphub.lib.sql_builder.core.SegmentProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NamedColumn implements Column {
    private final SegmentProvider segmentProvider;
    private final String name;

    @Override
    public String get() {
        return String.join(" as ", segmentProvider.get(), name);
    }
}
