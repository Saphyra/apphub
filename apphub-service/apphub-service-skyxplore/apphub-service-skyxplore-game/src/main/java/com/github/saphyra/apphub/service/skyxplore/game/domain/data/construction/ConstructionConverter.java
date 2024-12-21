package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.ConstructionResponse;
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
public class ConstructionConverter implements GameDataToModelConverter {
    public List<ConstructionModel> toModel(UUID gameId, Collection<Construction> constructions) {
        return constructions.stream()
            .map(construction -> toModel(gameId, construction))
            .collect(Collectors.toList());
    }

    public ConstructionModel toModel(UUID gameId, Construction construction) {
        ConstructionModel model = new ConstructionModel();
        model.setId(construction.getConstructionId());
        model.setGameId(gameId);
        model.setType(GameItemType.CONSTRUCTION);
        model.setConstructionType(construction.getConstructionType());
        model.setExternalReference(construction.getExternalReference());
        model.setLocation(construction.getLocation());
        model.setExternalReference(construction.getExternalReference());
        model.setRequiredWorkPoints(construction.getRequiredWorkPoints());
        model.setCurrentWorkPoints(construction.getCurrentWorkPoints());
        model.setPriority(construction.getPriority());
        model.setData(construction.getData());
        return model;
    }

    public ConstructionResponse toResponse(Construction construction) {
        return ConstructionResponse.builder()
            .constructionId(construction.getConstructionId())
            .requiredWorkPoints(construction.getRequiredWorkPoints())
            .currentWorkPoints(construction.getCurrentWorkPoints())
            .data(construction.getData())
            .build();
    }

    @Override
    public List<ConstructionModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getConstructions());
    }
}
