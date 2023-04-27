package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PopulationFillerServiceTest {
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private CitizenFactory citizenFactory;

    @Mock
    private StorageBuildingService storageBuildingService;

    @InjectMocks
    private PopulationFillerService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Planet emptyPlanet;

    @Mock
    private StorageBuilding storageBuilding;

    @Test
    void fillPopulation() {
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(
            new Planets(),
            new BiWrapper<>(UUID.randomUUID(), planet),
            new BiWrapper<>(UUID.randomUUID(), emptyPlanet)
        ));

        given(planet.hasOwner()).willReturn(true);
        given(emptyPlanet.hasOwner()).willReturn(false);

        given(storageBuildingService.findByStorageType(StorageType.CITIZEN)).willReturn(storageBuilding);
        given(storageBuilding.getCapacity()).willReturn(2);

        given(planet.getPlanetId()).willReturn(PLANET_ID);

        underTest.fillPopulation(gameData);

        verify(citizenFactory, times(2)).addToGameData(PLANET_ID, gameData);
    }
}