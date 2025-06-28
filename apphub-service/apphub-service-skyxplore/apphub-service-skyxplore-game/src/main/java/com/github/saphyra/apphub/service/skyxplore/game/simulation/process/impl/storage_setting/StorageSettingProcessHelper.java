package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request.ResourceRequestProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingProcessHelper {
    private final ResourceRequestProcessFactory resourceRequestProcessFactory;

    void orderResources(Game game, UUID location, UUID processId, UUID reservedStorageId) {
        resourceRequestProcessFactory.save(game, location, processId, reservedStorageId);
    }
}
