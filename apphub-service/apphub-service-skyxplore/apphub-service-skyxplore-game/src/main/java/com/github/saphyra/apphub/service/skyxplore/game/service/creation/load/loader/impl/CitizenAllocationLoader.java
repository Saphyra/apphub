package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CitizenAllocationLoader extends AutoLoader<CitizenAllocationModel, CitizenAllocation> {
    public CitizenAllocationLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.CITIZEN_ALLOCATION;
    }

    @Override
    protected Class<CitizenAllocationModel[]> getArrayClass() {
        return CitizenAllocationModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<CitizenAllocation> items) {
        gameData.getCitizenAllocations()
            .addAll(items);
    }

    @Override
    protected CitizenAllocation convert(CitizenAllocationModel model) {
        return CitizenAllocation.builder()
            .citizenAllocationId(model.getId())
            .citizenId(model.getCitizenId())
            .processId(model.getProcessId())
            .build();
    }
}
