package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_material;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class BodyMaterial {
    private final UUID id;
    private final UUID bodyId;
    private String material;
    private Double percent;
}
