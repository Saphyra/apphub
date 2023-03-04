package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.active;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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

    public ActiveMoraleRechargeProcess create(Game game, Planet planet, Citizen citizen) {
        return create(
            idGenerator.randomUuid(),
            ProcessStatus.CREATED,
            game,
            planet,
            citizen
        );
    }

    @Override
    public ActiveMoraleRechargeProcess createFromModel(Game game, ProcessModel model) {
        Planet planet = game.getUniverse()
            .findPlanetByIdValidated(model.getLocation());
        Citizen citizen = planet.getPopulation()
            .get(model.getExternalReference());
        return create(
            model.getId(),
            model.getStatus(),
            game,
            planet,
            citizen
        );
    }

    private ActiveMoraleRechargeProcess create(UUID processId, ProcessStatus status, Game game, Planet planet, Citizen citizen) {
        return ActiveMoraleRechargeProcess.builder()
            .processId(processId)
            .status(status)
            .game(game)
            .planet(planet)
            .citizen(citizen)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
