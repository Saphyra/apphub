package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructionProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;

    public ConstructionProcess create(GameData gameData, UUID location, Building building, Construction construction) {
        return ConstructionProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .gameData(gameData)
            .location(location)
            .building(building)
            .construction(construction)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Override
    public ProcessType getType() {
        return ProcessType.CONSTRUCTION;
    }

    @Override
    public ConstructionProcess createFromModel(Game game, ProcessModel model) {
        Construction construction = game.getData()
            .getConstructions()
            .findByConstructionIdValidated(model.getExternalReference());

        Building building = game.getData()
            .getBuildings()
            .findByBuildingId(construction.getExternalReference());

        return ConstructionProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .gameData(game.getData())
            .location(model.getLocation())
            .building(building)
            .construction(construction)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
