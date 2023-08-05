package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinateFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.planet.PlanetFiller;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system.NewbornSolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolarSystemFiller {
    private final SolarSystemFactory solarSystemFactory;
    private final PlanetFiller planetFiller;
    private final ReferredCoordinateFactory referredCoordinateFactory;

    public void fillNewbornSolarSystems(List<NewbornSolarSystem> newbornSolarSystems, GameData gameData, SkyXploreGameSettings settings) {
        for (NewbornSolarSystem newbornSolarSystem : newbornSolarSystems) {
            SolarSystem solarSystem = solarSystemFactory.create(newbornSolarSystem.getRadius(), gameData.getSolarSystems());
            ReferredCoordinate solarSystemCoordinate = referredCoordinateFactory.create(solarSystem.getSolarSystemId(), newbornSolarSystem.getCoordinate());

            gameData.getSolarSystems()
                .add(solarSystem);
            gameData.getCoordinates()
                .add(solarSystemCoordinate);

            planetFiller.fillPlanets(solarSystem, newbornSolarSystem.getPlanets(), gameData, settings);
        }
    }
}
