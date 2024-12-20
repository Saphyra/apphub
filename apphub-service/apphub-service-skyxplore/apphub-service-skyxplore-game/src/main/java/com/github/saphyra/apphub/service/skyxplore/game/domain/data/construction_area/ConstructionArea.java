package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class ConstructionArea {
    private final UUID constructionAreaId;
    private final UUID location;
    private final UUID surfaceId;
    private final String dataId;
}
