package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionLoader {
    private final GameItemLoader gameItemLoader;

    Deconstruction load(UUID externalReference) {
        return gameItemLoader.loadChildren(externalReference, GameItemType.DECONSTRUCTION, DeconstructionModel[].class)
            .stream()
            .findFirst()
            .map(this::convert)
            .orElse(null);
    }

    private Deconstruction convert(DeconstructionModel model) {
        return Deconstruction.builder()
            .deconstructionId(model.getId())
            .externalReference(model.getExternalReference())
            .currentWorkPoints(model.getCurrentWorkPoints())
            .priority(model.getPriority())
            .build();
    }
}
