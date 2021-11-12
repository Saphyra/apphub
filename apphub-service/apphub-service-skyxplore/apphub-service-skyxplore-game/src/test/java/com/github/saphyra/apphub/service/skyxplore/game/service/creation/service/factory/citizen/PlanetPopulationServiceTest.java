package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PlanetPopulationServiceTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    @Mock
    private StorageBuildingService storageBuildingService;

    @Mock
    private CitizenFactory citizenFactory;

    @InjectMocks
    private PlanetPopulationService underTest;

    @Mock
    private Planet planet;

    @Mock
    private StorageBuilding storageBuilding;

    @Mock
    private Citizen citizen;

    @Mock
    private OptionalHashMap<UUID, Citizen> populationMap;

    @Test
    public void addCitizens() {
        given(storageBuildingService.findByStorageType(StorageType.CITIZEN)).willReturn(storageBuilding);
        given(storageBuilding.getCapacity()).willReturn(1);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(citizenFactory.create(PLANET_ID)).willReturn(citizen);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(planet.getPopulation()).willReturn(populationMap);

        underTest.addCitizens(planet);

        verify(populationMap).put(CITIZEN_ID, citizen);
    }
}