package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
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
    private StorageCapacityService storageCapacityService;

    @InjectMocks
    private PopulationFillerService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Planet emptyPlanet;

    @Test
    void fillPopulation() {
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(
            new Planets(),
            new BiWrapper<>(UUID.randomUUID(), planet),
            new BiWrapper<>(UUID.randomUUID(), emptyPlanet)
        ));

        given(planet.hasOwner()).willReturn(true);
        given(emptyPlanet.hasOwner()).willReturn(false);

        given(storageCapacityService.calculateDwellingCapacity(gameData, PLANET_ID)).willReturn(2);

        given(planet.getPlanetId()).willReturn(PLANET_ID);

        underTest.fillPopulation(gameData);

        verify(citizenFactory, times(2)).addToGameData(PLANET_ID, gameData);
    }
}