package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class BuildingModule {
    private final UUID buildingModuleId;
    private final UUID location;
    private final UUID constructionAreaId;
    private final String dataId;
}
