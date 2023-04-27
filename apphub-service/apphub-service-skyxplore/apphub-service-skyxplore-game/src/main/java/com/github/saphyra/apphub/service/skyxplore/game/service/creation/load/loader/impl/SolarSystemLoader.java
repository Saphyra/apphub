package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SolarSystemLoader extends AutoLoader<SolarSystemModel, SolarSystem> {
    public SolarSystemLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.SOLAR_SYSTEM;
    }

    @Override
    protected Class<SolarSystemModel[]> getArrayClass() {
        return SolarSystemModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<SolarSystem> items) {
        gameData.getSolarSystems()
            .addAll(items);
    }

    @Override
    protected SolarSystem convert(SolarSystemModel solarSystemModel) {
        return SolarSystem.builder()
            .solarSystemId(solarSystemModel.getId())
            .radius(solarSystemModel.getRadius())
            .defaultName(solarSystemModel.getDefaultName())
            .customNames(new OptionalHashMap<>(solarSystemModel.getCustomNames()))
            .build();
    }
}
