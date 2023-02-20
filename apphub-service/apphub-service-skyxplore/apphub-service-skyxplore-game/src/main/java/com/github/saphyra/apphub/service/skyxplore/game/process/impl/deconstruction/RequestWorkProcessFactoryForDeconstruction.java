package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
//TODO unit test
class RequestWorkProcessFactoryForDeconstruction {
    private final RequestWorkProcessFactory requestWorkProcessFactory;
    private final GameProperties gameProperties;

    List<RequestWorkProcess> createRequestWorkProcesses(Game game, UUID processId, Planet planet, UUID deconstructionId) {
        DeconstructionProperties deconstructionProperties = gameProperties.getDeconstruction();

        return requestWorkProcessFactory.create(
            game,
            processId,
            planet,
            deconstructionId,
            RequestWorkProcessType.DECONSTRUCTION,
            SkillType.BUILDING,
            deconstructionProperties.getRequiredWorkPoints(),
            deconstructionProperties.getParallelWorkers()
        );
    }
}
