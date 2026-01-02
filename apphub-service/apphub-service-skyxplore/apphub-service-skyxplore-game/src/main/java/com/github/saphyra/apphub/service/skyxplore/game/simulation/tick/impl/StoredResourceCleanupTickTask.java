package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredResourceCleanupTickTask implements TickTask {
    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.STORED_RESOURCE_CLEANUP;
    }

    @Override
    public void process(Game game) {
        List<UUID> convoys = game.getData()
            .getConvoys()
            .stream()
            .map(Convoy::getConvoyId)
            .toList();

        List<StoredResource> toDelete = game.getData()
            .getStoredResources()
            .stream()
            .filter(storedResource -> storedResource.getAmount() <= 0 && isNull(storedResource.getAllocatedBy()))
            //Remove after fixed: Convoys should not pick up 0 amount resources
            .filter(storedResource -> !convoys.contains(storedResource.getContainerId()))
            .toList();


        toDelete.forEach(storedResource -> {
            game.getData().getStoredResources().remove(storedResource);
            game.getProgressDiff().delete(storedResource.getStoredResourceId(), GameItemType.STORED_RESOURCE);
        });

        log.info("Cleaned up {} stored resources in game {}", toDelete.size(), game.getGameId());
    }
}
