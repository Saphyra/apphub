package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorageCalculatorTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer CAPACITY = 34;
    private static final Integer LEVEL = 2;
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private StorageBuildingService storageBuildingService;

    @InjectMocks
    private StorageCalculator underTest;

    @Mock
    private GameData gameData;

    @Mock
    private StorageBuildingData storageBuildingData;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Mock
    private Building deconstructedBuilding;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Test
    void calculateCapacity() {
        given(storageBuildingService.findByStorageType(StorageType.CITIZEN)).willReturn(storageBuildingData);
        given(gameData.getBuildings()).willReturn(buildings);
        given(storageBuildingData.getId()).willReturn(DATA_ID);
        given(buildings.getByLocationAndDataId(LOCATION, DATA_ID)).willReturn(List.of(building, deconstructedBuilding));
        given(storageBuildingData.getCapacity()).willReturn(CAPACITY);
        given(building.getLevel()).willReturn(LEVEL);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructedBuilding.getBuildingId()).willReturn(BUILDING_ID);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(deconstruction));
        given(deconstructions.findByExternalReference(null)).willReturn(Optional.empty());

        int result = underTest.calculateCapacity(gameData, LOCATION, StorageType.CITIZEN);

        assertThat(result).isEqualTo(CAPACITY * LEVEL);
    }
}