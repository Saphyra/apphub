package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionUpdateService {
    private final DeconstructionConverter deconstructionConverter;

    void updateDeconstruction(GameProgressDiff progressDiff, GameData gameData, UUID deconstructionId, int completedWorkPoints) {
        log.info("Adding {} workPoints to DECONSTRUCTION {}", completedWorkPoints, deconstructionId);

        Deconstruction deconstruction = gameData.getDeconstructions()
            .findByDeconstructionId(deconstructionId);

        log.info("Before update: {}", deconstruction);
        deconstruction.increaseWorkPoints(completedWorkPoints);
        log.info("After update: {}", deconstruction);

        progressDiff.save(deconstructionConverter.toModel(gameData.getGameId(), deconstruction));
    }
}
