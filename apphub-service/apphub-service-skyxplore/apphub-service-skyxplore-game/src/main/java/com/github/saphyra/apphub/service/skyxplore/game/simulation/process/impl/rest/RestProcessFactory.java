package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;


import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class RestProcessFactory implements ProcessFactory {
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    @Override
    public ProcessType getType() {
        return ProcessType.REST;
    }

    public RestProcess create(GameData gameData, Citizen citizen, int restForTicks) {
        return RestProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .gameData(gameData)
            .citizenId(citizen.getCitizenId())
            .location(citizen.getLocation())
            .restForTicks(restForTicks)
            .restedForTicks(0)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Override
    public RestProcess createFromModel(Game game, ProcessModel model) {
        return RestProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .gameData(game.getData())
            .citizenId(model.getExternalReference())
            .location(model.getLocation())
            .restForTicks(Integer.parseInt(model.getData().get(ProcessParamKeys.REST_FOR_TICKS)))
            .restedForTicks(Integer.parseInt(model.getData().get(ProcessParamKeys.RESTED_FOR_TICKS)))
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
