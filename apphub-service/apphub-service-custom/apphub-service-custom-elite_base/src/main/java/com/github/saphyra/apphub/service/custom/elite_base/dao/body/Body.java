package com.github.saphyra.apphub.service.custom.elite_base.dao.body;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Body {
    private final UUID id;
    private LocalDateTime lastUpdate;
    private UUID starSystemId;
    private BodyType type;
    private Long bodyId;
    private String bodyName;
    private Double distanceFromStar;
}
