package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.active;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActiveMoraleRechargeProcessFactory implements ProcessFactory {
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    @Override
    public ProcessType getType() {
        return ProcessType.ACTIVE_MORALE_RECHARGE;
    }

    public ActiveMoraleRechargeProcess create(GameData gameData, UUID location, Citizen citizen) {
        return create(
            idGenerator.randomUuid(),
            ProcessStatus.CREATED,
            gameData,
            location,
            citizen
        );
    }

    @Override
    public ActiveMoraleRechargeProcess createFromModel(Game game, ProcessModel model) {
        Citizen citizen = game.getData()
            .getCitizens()
            .findByCitizenIdValidated(model.getExternalReference());
        return create(
            model.getId(),
            model.getStatus(),
            game.getData(),
            model.getLocation(),
            citizen
        );
    }

    private ActiveMoraleRechargeProcess create(UUID processId, ProcessStatus status, GameData gameData, UUID location, Citizen citizen) {
        return ActiveMoraleRechargeProcess.builder()
            .processId(processId)
            .status(status)
            .gameData(gameData)
            .location(location)
            .citizen(citizen)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
