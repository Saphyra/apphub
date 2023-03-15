package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ReservedStorageLoader {
    private final GameItemLoader gameItemLoader;

    ReservedStorages load(UUID location) {
        List<ReservedStorageModel> models = gameItemLoader.loadChildren(location, GameItemType.RESERVED_STORAGE, ReservedStorageModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toCollection(ReservedStorages::new));
    }

    private ReservedStorage convert(ReservedStorageModel model) {
        return ReservedStorage.builder()
            .reservedStorageId(model.getId())
            .location(model.getLocation())
            .locationType(LocationType.valueOf(model.getLocationType()))
            .externalReference(model.getExternalReference())
            .dataId(model.getDataId())
            .amount(model.getAmount())
            .build();
    }
}
