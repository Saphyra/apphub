package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorageCalculatorTest {
    private static final Integer HQ_CAPACITY = 32;
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String STORAGE_BUILDING_DATA_ID = "storage-building-data-id";
    private static final UUID STORAGE_BUILDING_ID = UUID.randomUUID();
    private static final UUID HQ_BUILDING_ID = UUID.randomUUID();
    private static final Integer HQ_BUILDING_LEVEL = 4;
    private static final Integer STORAGE_BUILDING_LEVEL = 2;
    private static final Integer STORAGE_BUILDING_CAPACITY = 56;

    @Mock
    private StorageBuildingService storageBuildingService;

    @Mock
    private HeadquartersUtil headquartersUtil;

    @InjectMocks
    private StorageCalculator underTest;

    @Mock
    private StorageBuildingData storageBuildingData;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Mock
    private Building storageBuilding;

    @Mock
    private Building hqBuilding;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @BeforeEach
    void setUp() {
        given(storageBuildingService.findByStorageType(StorageType.BULK)).willReturn(storageBuildingData);
        given(headquartersUtil.getStores()).willReturn(Map.of(StorageType.BULK, HQ_CAPACITY));
        given(gameData.getBuildings()).willReturn(buildings);
        given(storageBuildingData.getId()).willReturn(STORAGE_BUILDING_DATA_ID);
        given(buildings.getByLocationAndDataId(LOCATION, STORAGE_BUILDING_DATA_ID)).willReturn(List.of(storageBuilding));
        given(buildings.getByLocationAndDataId(LOCATION, GameConstants.DATA_ID_HEADQUARTERS)).willReturn(List.of(hqBuilding));
        given(storageBuilding.getBuildingId()).willReturn(STORAGE_BUILDING_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(hqBuilding.getBuildingId()).willReturn(HQ_BUILDING_ID);
    }

    @Test
    void productionBuildingUnderDeconstruction() {
        given(deconstructions.findByExternalReference(STORAGE_BUILDING_ID)).willReturn(Optional.of(deconstruction));
        given(deconstructions.findByExternalReference(HQ_BUILDING_ID)).willReturn(Optional.empty());
        given(hqBuilding.getLevel()).willReturn(HQ_BUILDING_LEVEL);


        assertThat(underTest.calculateCapacity(gameData, LOCATION, StorageType.BULK)).isEqualTo(HQ_BUILDING_LEVEL * HQ_CAPACITY);
    }

    @Test
    void hqUnderDeconstruction() {
        given(deconstructions.findByExternalReference(STORAGE_BUILDING_ID)).willReturn(Optional.empty());
        given(deconstructions.findByExternalReference(HQ_BUILDING_ID)).willReturn(Optional.of(deconstruction));
        given(storageBuilding.getLevel()).willReturn(STORAGE_BUILDING_LEVEL);
        given(storageBuildingData.getCapacity()).willReturn(STORAGE_BUILDING_CAPACITY);


        assertThat(underTest.calculateCapacity(gameData, LOCATION, StorageType.BULK)).isEqualTo(STORAGE_BUILDING_LEVEL * STORAGE_BUILDING_CAPACITY);
    }

    @Test
    void noDeconstruction() {
        given(deconstructions.findByExternalReference(STORAGE_BUILDING_ID)).willReturn(Optional.empty());
        given(deconstructions.findByExternalReference(HQ_BUILDING_ID)).willReturn(Optional.empty());
        given(storageBuilding.getLevel()).willReturn(STORAGE_BUILDING_LEVEL);
        given(hqBuilding.getLevel()).willReturn(HQ_BUILDING_LEVEL);
        given(storageBuildingData.getCapacity()).willReturn(STORAGE_BUILDING_CAPACITY);


        assertThat(underTest.calculateCapacity(gameData, LOCATION, StorageType.BULK)).isEqualTo(HQ_BUILDING_LEVEL * HQ_CAPACITY + STORAGE_BUILDING_LEVEL * STORAGE_BUILDING_CAPACITY);
    }
}