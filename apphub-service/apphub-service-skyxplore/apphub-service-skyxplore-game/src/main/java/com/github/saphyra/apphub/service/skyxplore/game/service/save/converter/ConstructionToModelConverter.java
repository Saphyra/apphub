package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructionToModelConverter {
    public ConstructionModel convert(Construction construction, UUID gameId) {
        ConstructionModel model = new ConstructionModel();
        model.setId(construction.getConstructionId());
        model.setGameId(gameId);
        model.setType(GameItemType.CONSTRUCTION);
        model.setLocation(construction.getLocation());
        model.setLocationType(construction.getLocationType().name());
        model.setRequiredWorkPoints(construction.getRequiredWorkPoints());
        model.setCurrentWorkPoints(construction.getCurrentWorkPoints());
        model.setPriority(construction.getPriority());
        return model;
    }
}
