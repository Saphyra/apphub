package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.Durability;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DurabilityLoader  extends AutoLoader<DurabilityModel, Durability> {
    public DurabilityLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.DURABILITY;
    }

    @Override
    protected Class<DurabilityModel[]> getArrayClass() {
        return DurabilityModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Durability> items) {
        gameData.getDurabilities()
            .addAll(items);
    }

    @Override
    protected Durability convert(DurabilityModel model) {
        return Durability.builder()
            .durabilityId(model.getId())
            .externalReference(model.getExternalReference())
            .maxHitPoints(model.getMaxHitPoints())
            .currentHitPoints(model.getCurrentHitPoints())
            .build();
    }
}
