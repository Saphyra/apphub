package com.github.saphyra.apphub.service.utils.sql_generator.dao.segment;

import com.github.saphyra.apphub.api.utils.model.sql_generator.SegmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Segment {
    private final UUID segmentId;
    private final UUID userId;
    private final UUID externalReference;
    private final SegmentType segmentType;
    private final Integer order;
}
