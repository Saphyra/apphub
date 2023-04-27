package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CitizenLoader extends AutoLoader<CitizenModel, Citizen> {
    public CitizenLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.CITIZEN;
    }

    @Override
    protected Class<CitizenModel[]> getArrayClass() {
        return CitizenModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Citizen> items) {
        gameData.getCitizens()
            .addAll(items);
    }

    @Override
    protected Citizen convert(CitizenModel model) {
        return Citizen.builder()
            .citizenId(model.getId())
            .location(model.getLocation())
            .name(model.getName())
            .morale(model.getMorale())
            .satiety(model.getSatiety())
            .build();
    }
}
