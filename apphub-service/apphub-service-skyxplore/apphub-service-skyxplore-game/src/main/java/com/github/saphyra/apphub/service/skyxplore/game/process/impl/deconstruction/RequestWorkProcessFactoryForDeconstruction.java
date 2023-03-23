package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class RequestWorkProcessFactoryForDeconstruction {
    private final RequestWorkProcessFactory requestWorkProcessFactory;
    private final GameProperties gameProperties;

    List<RequestWorkProcess> createRequestWorkProcesses(GameData gameData, UUID location ,UUID processId,  UUID deconstructionId) {
        DeconstructionProperties deconstructionProperties = gameProperties.getDeconstruction();

        return requestWorkProcessFactory.create(
            gameData,
            processId,
            location,
            deconstructionId,
            RequestWorkProcessType.DECONSTRUCTION,
            SkillType.BUILDING,
            deconstructionProperties.getRequiredWorkPoints(),
            deconstructionProperties.getParallelWorkers()
        );
    }
}
