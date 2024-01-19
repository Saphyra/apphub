package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionProcessHelper {
    private final WorkProcessFactory workProcessFactory;

    void startWork(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID location, UUID deconstructionId) {
        workProcessFactory.createForDeconstruction(gameData, processId, deconstructionId, location)
            .forEach(workProcess -> {
                gameData.getProcesses()
                    .add(workProcess);
                progressDiff.save(workProcess.toModel());
            });
    }

    void finishDeconstruction(GameProgressDiff progressDiff, GameData gameData, UUID deconstructionId) {
        log.info("Finishing deconstruction...");

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionId(deconstructionId);

        Building building = gameData.getBuildings()
            .findByBuildingId(deconstruction.getExternalReference());

        gameData.getBuildings()
            .remove(building);

        gameData.getDeconstructions()
            .remove(deconstruction);

        progressDiff.delete(deconstructionId, GameItemType.DECONSTRUCTION);
        progressDiff.delete(building.getBuildingId(), GameItemType.BUILDING);
    }
}
