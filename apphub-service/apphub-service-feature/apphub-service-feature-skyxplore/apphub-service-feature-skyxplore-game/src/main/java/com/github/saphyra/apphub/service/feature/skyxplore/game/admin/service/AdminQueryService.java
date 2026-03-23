package com.github.saphyra.apphub.service.feature.skyxplore.game.admin.service;

import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataDetails;
import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataEntry;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface AdminQueryService {
    GameItemType getType();

    List<SkyXploreGameDataEntry> getAll(@Nullable UUID gameId);


    SkyXploreGameDataDetails findById(UUID gameId, UUID itemId);
}
