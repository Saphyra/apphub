package com.github.saphyra.apphub.lib.sql_builder;

public interface SegmentProvider {
    SegmentProvider AND_SEGMENT = () -> "AND";
    SegmentProvider OR_SEGMENT = () -> "OR";
    Column ALL_SEGMENT = () -> "*";

    String get();
}
