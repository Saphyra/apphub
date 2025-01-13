package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class BodyRing {
    private final UUID id;
    private final UUID bodyId;
    private String name;
    private RingType type;
    private Double innerRadius;
    private Double outerRadius;
    private Double mass;
}
