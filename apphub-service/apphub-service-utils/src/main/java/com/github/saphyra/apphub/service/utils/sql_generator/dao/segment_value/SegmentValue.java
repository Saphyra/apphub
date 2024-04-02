package com.github.saphyra.apphub.service.utils.sql_generator.dao.segment_value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class SegmentValue {
    private final UUID segmentValueId;
    private final UUID userId;
    private final UUID externalReference;
    private final String segmentValue;
}
