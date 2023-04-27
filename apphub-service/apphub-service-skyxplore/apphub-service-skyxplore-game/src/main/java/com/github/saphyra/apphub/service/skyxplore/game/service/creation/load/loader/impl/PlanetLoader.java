package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanetLoader extends AutoLoader<PlanetModel, Planet> {
    public PlanetLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.PLANET;
    }

    @Override
    protected Class<PlanetModel[]> getArrayClass() {
        return PlanetModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Planet> items) {
        items.forEach(planet -> gameData.getPlanets().put(planet.getPlanetId(), planet));
    }

    @Override
    protected Planet convert(PlanetModel model) {
        return Planet.builder()
            .planetId(model.getId())
            .solarSystemId(model.getSolarSystemId())
            .defaultName(model.getDefaultName())
            .customNames(new OptionalHashMap<>(model.getCustomNames()))
            .size(model.getSize())
            .orbitRadius(model.getOrbitRadius())
            .orbitSpeed(model.getOrbitSpeed())
            .owner(model.getOwner())
            .build();
    }
}
