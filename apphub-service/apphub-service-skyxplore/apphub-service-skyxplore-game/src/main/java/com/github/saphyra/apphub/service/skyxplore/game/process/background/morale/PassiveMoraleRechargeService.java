package com.github.saphyra.apphub.service.skyxplore.game.process.background.morale;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
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

    public void processGame(GameData gameData, SyncCache syncCache) {
        gameData.getCitizens()
            .forEach(citizen -> processCitizen(gameData, citizen, syncCache));
    }

    private void processCitizen(GameData gameData, Citizen citizen, SyncCache syncCache) {
        log.debug("Decreasing satiety for citizen {}", citizen.getCitizenId());
        if (citizen.getMorale() < gameProperties.getCitizen().getMorale().getMax() && gameData.getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isEmpty()) {
            Process process = passiveMoraleRechargeProcessFactory.create(gameData, citizen);
            syncCache.saveGameItem(process.toModel());

            gameData.getProcesses()
                .add(process);
        }
    }
}
