package com.github.saphyra.apphub.lib.sql_builder.value;

import com.github.saphyra.apphub.lib.sql_builder.core.SegmentProvider;

public class NullValue implements SegmentProvider {
    @Override
    public String get() {
        return "null";
    }
}
