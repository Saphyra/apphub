package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorageCalculatorTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer CAPACITY = 34;
    private static final Integer LEVEL = 2;

    @Mock
    private StorageBuildingService storageBuildingService;

    @InjectMocks
    private StorageCalculator underTest;

    @Mock
    private GameData gameData;

    @Mock
    private StorageBuilding storageBuilding;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Test
    void calculateCapacity() {
        given(storageBuildingService.findByStorageType(StorageType.CITIZEN)).willReturn(storageBuilding);
        given(gameData.getBuildings()).willReturn(buildings);
        given(storageBuilding.getId()).willReturn(DATA_ID);
        given(buildings.getByLocationAndDataId(LOCATION, DATA_ID)).willReturn(List.of(building));
        given(storageBuilding.getCapacity()).willReturn(CAPACITY);
        given(building.getLevel()).willReturn(LEVEL);

        int result = underTest.calculateCapacity(gameData, LOCATION, StorageType.CITIZEN);

        assertThat(result).isEqualTo(CAPACITY * LEVEL);
    }
}