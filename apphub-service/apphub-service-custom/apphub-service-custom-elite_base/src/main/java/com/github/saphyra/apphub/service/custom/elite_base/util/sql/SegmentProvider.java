package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

public interface SegmentProvider {
    SegmentProvider AND_SEGMENT = () -> "AND";
    SegmentProvider OR_SEGMENT = () -> "OR";
    Column ALL_SEGMENT = () -> "*";

    String get();
}
