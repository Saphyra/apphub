package com.github.saphyra.apphub.service.skyxplore.game.process.background.morale;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive.PassiveMoraleRechargeProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassiveMoraleRechargeService {
    private final GameProperties gameProperties;
    private final PassiveMoraleRechargeProcessFactory passiveMoraleRechargeProcessFactory;

    public void processGame(Game game, SyncCache syncCache) {
        game.getData()
            .getCitizens()
            .forEach(citizen -> processCitizen(game, citizen, syncCache));
    }

    private void processCitizen(Game game, Citizen citizen, SyncCache syncCache) {
        log.debug("Decreasing satiety for citizen {}", citizen.getCitizenId());
        int maxMorale = gameProperties.getCitizen()
            .getMorale()
            .getMax();
        boolean citizenAllocated = game.getData()
            .getCitizenAllocations()
            .findByCitizenId(citizen.getCitizenId())
            .isEmpty();
        if (citizen.getMorale() < maxMorale && citizenAllocated) {
            Process process = passiveMoraleRechargeProcessFactory.create(game, citizen);
            syncCache.saveGameItem(process.toModel());

            game.getData()
                .getProcesses()
                .add(process);
        }
    }
}
