package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class AllocatedResourceLoader {
    private final GameItemLoader gameItemLoader;

    Vector<AllocatedResource> load(UUID location) {
        List<AllocatedResourceModel> models = gameItemLoader.loadChildren(location, GameItemType.ALLOCATED_RESOURCE, AllocatedResourceModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toCollection(Vector::new));
    }

    private AllocatedResource convert(AllocatedResourceModel model) {
        return AllocatedResource.builder()
            .allocatedResourceId(model.getId())
            .location(model.getLocation())
            .locationType(LocationType.valueOf(model.getLocationType()))
            .externalReference(model.getExternalReference())
            .dataId(model.getDataId())
            .amount(model.getAmount())
            .build();
    }
}
