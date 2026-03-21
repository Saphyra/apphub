package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class BuildingModule {
    private final UUID buildingModuleId;
    private final UUID location;
    private final UUID constructionAreaId;
    private final String dataId;
    @Builder.Default
    private boolean existing = false;
}
