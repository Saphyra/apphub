package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.solar_system;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SolarSystemNames;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.NewbornSolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SolarSystemFactory {
    private final IdGenerator idGenerator;
    private final SolarSystemNames solarSystemNames;

    public SolarSystem create(NewbornSolarSystem newbornSolarSystem, Collection<SolarSystem> solarSystems) {
        return SolarSystem.builder()
            .solarSystemId(idGenerator.randomUuid())
            .radius(newbornSolarSystem.getRadius())
            .defaultName(solarSystemNames.getRandomStarName(solarSystems.stream().map(SolarSystem::getDefaultName).toList()))
            .build();
    }
}
