package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PriorityLoader extends AutoLoader<PriorityModel, Priority> {
    public PriorityLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.PRIORITY;
    }

    @Override
    protected Class<PriorityModel[]> getArrayClass() {
        return PriorityModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Priority> items) {
        gameData.getPriorities()
            .addAll(items);
    }

    @Override
    protected Priority convert(PriorityModel model) {
        return Priority.builder()
            .priorityId(model.getId())
            .location(model.getLocation())
            .type(PriorityType.valueOf(model.getPriorityType()))
            .value(model.getValue())
            .build();
    }
}
