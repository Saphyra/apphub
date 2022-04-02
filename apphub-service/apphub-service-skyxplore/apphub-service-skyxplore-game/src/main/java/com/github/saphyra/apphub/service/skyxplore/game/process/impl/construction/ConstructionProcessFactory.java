package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ConstructionProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;

    public ConstructionProcess create(Game game, Planet planet, Building building) {
        return ConstructionProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .game(game)
            .planet(planet)
            .building(building)
            .construction(building.getConstruction())
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
