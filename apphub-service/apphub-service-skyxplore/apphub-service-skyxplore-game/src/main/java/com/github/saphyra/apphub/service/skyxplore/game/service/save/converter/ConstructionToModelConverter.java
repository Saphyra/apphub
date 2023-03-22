package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
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
public class ConstructionToModelConverter {
    public List<ConstructionModel> convert(UUID gameId, Collection<Construction> constructions) {
        return constructions.stream()
            .map(construction -> convert(gameId, construction))
            .collect(Collectors.toList());
    }

    public ConstructionModel convert(UUID gameId, Construction construction) {
        ConstructionModel model = new ConstructionModel();
        model.setId(construction.getConstructionId());
        model.setGameId(gameId);
        model.setType(GameItemType.CONSTRUCTION);
        model.setExternalReference(construction.getExternalReference());
        model.setExternalReference(construction.getExternalReference());
        model.setParallelWorkers(construction.getParallelWorkers());
        model.setRequiredWorkPoints(construction.getRequiredWorkPoints());
        model.setCurrentWorkPoints(construction.getCurrentWorkPoints());
        model.setPriority(construction.getPriority());
        model.setData(construction.getData());
        return model;
    }
}
