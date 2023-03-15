package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassiveMoraleRechargeProcessFactory implements ProcessFactory {
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    public PassiveMoraleRechargeProcess create(Game game, Planet planet, Citizen citizen) {
        return create(
            idGenerator.randomUuid(),
            ProcessStatus.CREATED,
            game,
            planet,
            citizen
        );
    }

    @Override
    public ProcessType getType() {
        return ProcessType.PASSIVE_MORALE_RECHARGE;
    }

    @Override
    public PassiveMoraleRechargeProcess createFromModel(Game game, ProcessModel model) {
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

    private PassiveMoraleRechargeProcess create(UUID processId, ProcessStatus status, Game game, Planet planet, Citizen citizen) {
        return PassiveMoraleRechargeProcess.builder()
            .processId(processId)
            .status(status)
            .game(game)
            .planet(planet)
            .citizen(citizen)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}