package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredResourceMergerTickTask implements TickTask {
    private final StoredResourceConverter storedResourceConverter;

    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.STORED_RESOURCE_MERGER;
    }

    @Override
    public void process(Game game) {
        List<List<StoredResource>> toMerge = game.getData()
            .getStoredResources()
            .stream()
            .filter(storedResource -> storedResource.getAmount() > 0)
            .collect(Collectors.groupingBy(o -> new StoredResourceKey(o.getDataId(), o.getAllocatedBy(), o.getContainerId())))
            .values()
            .stream()
            .filter(storedResources -> storedResources.size() > 1)
            .toList();

        toMerge.forEach(storedResources -> merge(game, storedResources));
    }

    private void merge(Game game, List<StoredResource> storedResources) {
        StoredResource main = storedResources.getFirst();
        GameProgressDiff progressDiff = game.getProgressDiff();

        for (int i = 1; i < storedResources.size(); i++) {
            StoredResource toBeMerged = storedResources.get(i);
            main.increaseAmount(toBeMerged.getAmount());
            game.getData()
                .getStoredResources()
                .remove(toBeMerged);
            progressDiff.delete(toBeMerged.getStoredResourceId(), GameItemType.STORED_RESOURCE);
            log.debug("{} merged into {}", toBeMerged, main);
        }

        progressDiff.save(storedResourceConverter.toModel(game.getGameId(), main));

        log.info("{} StoredResources merged into {}", storedResources.size(), main);
    }

    private record StoredResourceKey(String dataId, UUID allocatedBy, UUID containerId) {
    }
}
