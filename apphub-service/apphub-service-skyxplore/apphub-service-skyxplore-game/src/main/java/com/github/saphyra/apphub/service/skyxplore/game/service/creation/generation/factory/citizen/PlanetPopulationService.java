package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.citizen;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetPopulationService {
    private final CitizenFactory citizenFactory;
    private final StorageBuildingService storageBuildingService;

    public void addCitizens(Planet planet) {
        int capacity = storageBuildingService.findByStorageType(StorageType.CITIZEN)
            .getCapacity();

        Stream.generate(() -> null)
            .limit(capacity)
            .map(o -> citizenFactory.create(planet.getPlanetId()))
            .forEach(citizen -> planet.getPopulation().put(citizen.getCitizenId(), citizen));
    }
}
