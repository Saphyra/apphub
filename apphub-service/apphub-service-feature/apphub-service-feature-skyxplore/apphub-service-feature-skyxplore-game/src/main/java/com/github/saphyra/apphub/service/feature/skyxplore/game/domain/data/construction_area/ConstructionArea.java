package com.github.saphyra.apphub.service.feature.skyxplore.game.domain.data.construction_area;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class ConstructionArea {
    private final UUID constructionAreaId;
    private final UUID location;
    private final UUID surfaceId;
    private final String dataId;
    @Builder.Default
    private boolean existing = false;
}
