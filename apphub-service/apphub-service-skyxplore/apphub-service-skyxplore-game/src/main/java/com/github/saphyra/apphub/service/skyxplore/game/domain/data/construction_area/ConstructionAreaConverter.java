package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceConstructionAreaResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class ConstructionAreaConverter implements GameDataToModelConverter {
    private final ConstructionConverter constructionConverter;
    private final DeconstructionConverter deconstructionConverter;

    @Override
    public List<ConstructionAreaModel> convert(UUID gameId, GameData gameData) {
        return gameData.getConstructionAreas()
            .stream()
            .map(constructionArea -> convert(gameId, constructionArea))
            .toList();
    }

    private ConstructionAreaModel convert(UUID gameId, ConstructionArea buildingModule) {
        ConstructionAreaModel model = new ConstructionAreaModel();
        model.setId(buildingModule.getConstructionAreaId());
        model.setGameId(gameId);
        model.setType(GameItemType.CONSTRUCTION_AREA);
        model.setLocation(buildingModule.getLocation());
        model.setSurfaceId(buildingModule.getSurfaceId());
        model.setDataId(buildingModule.getDataId());
        return model;
    }

    public SurfaceConstructionAreaResponse toResponse(GameData gameData, ConstructionArea constructionArea) {
        return SurfaceConstructionAreaResponse.builder()
            .constructionAreaId(constructionArea.getConstructionAreaId())
            .dataId(constructionArea.getDataId())
            .construction(gameData.getConstructions().findByExternalReference(constructionArea.getConstructionAreaId()).map(constructionConverter::toResponse).orElse(null))
            .deconstruction(gameData.getDeconstructions().findByExternalReference(constructionArea.getConstructionAreaId()).map(deconstructionConverter::toResponse).orElse(null))
            .build();
    }
}
