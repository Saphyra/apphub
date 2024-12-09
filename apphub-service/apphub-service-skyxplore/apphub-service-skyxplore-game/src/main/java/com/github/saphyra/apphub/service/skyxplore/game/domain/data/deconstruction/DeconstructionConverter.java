package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.DeconstructionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructionConverter implements GameDataToModelConverter {
    private final GameProperties gameProperties;

    public List<DeconstructionModel> toModel(UUID gameId, Collection<Deconstruction> deconstructions) {
        return deconstructions.stream()
            .map(deconstruction -> toModel(gameId, deconstruction))
            .collect(Collectors.toList());
    }

    public DeconstructionModel toModel(UUID gameId, Deconstruction deconstruction) {
        DeconstructionModel model = new DeconstructionModel();
        model.setId(deconstruction.getDeconstructionId());
        model.setGameId(gameId);
        model.setType(GameItemType.DECONSTRUCTION);
        model.setExternalReference(deconstruction.getExternalReference());
        model.setLocation(deconstruction.getLocation());
        model.setCurrentWorkPoints(deconstruction.getCurrentWorkPoints());
        model.setPriority(deconstruction.getPriority());
        return model;
    }

    public DeconstructionResponse toResponse(Deconstruction deconstruction) {
        return DeconstructionResponse.builder()
            .deconstructionId(deconstruction.getDeconstructionId())
            .requiredWorkPoints(gameProperties.getDeconstruction().getRequiredWorkPoints())
            .currentWorkPoints(deconstruction.getCurrentWorkPoints())
            .build();
    }

    @Override
    public List<DeconstructionModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getDeconstructions());
    }
}
