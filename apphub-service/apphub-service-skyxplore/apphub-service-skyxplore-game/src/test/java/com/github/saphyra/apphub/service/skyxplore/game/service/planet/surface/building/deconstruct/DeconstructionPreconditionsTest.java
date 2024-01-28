package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceAmountQueryService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DeconstructionPreconditionsTest {
    private static final String DATA_ID = "data-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer TOTAL_CAPACITY = 1000;
    private static final Integer CAPACITY = 100;
    private static final Integer LEVEL = 5;

    @Mock
    private StorageBuildingService storageBuildingService;

    @Mock
    private StorageCalculator storageCalculator;

    @Mock
    private StoredResourceAmountQueryService storedResourceAmountQueryService;

    @InjectMocks
    private DeconstructionPreconditions underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Building building;

    @Mock
    private StorageBuildingData storageBuildingData;

    @Test
    void notStorageBuilding() {
        given(building.getDataId()).willReturn(DATA_ID);
        given(storageBuildingService.get(DATA_ID)).willReturn(null);

        underTest.checkIfBuildingCanBeDeconstructed(gameData, building);

        verifyNoInteractions(storageCalculator);
        verifyNoInteractions(storedResourceAmountQueryService);
    }

    @Test
    void hasEnoughCapacity() {
        given(building.getDataId()).willReturn(DATA_ID);
        given(storageBuildingService.get(DATA_ID)).willReturn(storageBuildingData);
        given(storageBuildingData.getStores()).willReturn(StorageType.BULK);
        given(building.getLocation()).willReturn(LOCATION);
        given(storageCalculator.calculateCapacity(gameData, LOCATION, StorageType.BULK)).willReturn(TOTAL_CAPACITY);
        given(storageBuildingData.getCapacity()).willReturn(CAPACITY);
        given(building.getLevel()).willReturn(LEVEL);
        given(storedResourceAmountQueryService.getActualAmount(gameData, LOCATION, StorageType.BULK)).willReturn(TOTAL_CAPACITY - LEVEL * CAPACITY);

        underTest.checkIfBuildingCanBeDeconstructed(gameData, building);
    }

    @Test
    void notEnoughCapacity() {
        given(building.getDataId()).willReturn(DATA_ID);
        given(storageBuildingService.get(DATA_ID)).willReturn(storageBuildingData);
        given(storageBuildingData.getStores()).willReturn(StorageType.BULK);
        given(building.getLocation()).willReturn(LOCATION);
        given(storageCalculator.calculateCapacity(gameData, LOCATION, StorageType.BULK)).willReturn(TOTAL_CAPACITY);
        given(storageBuildingData.getCapacity()).willReturn(CAPACITY);
        given(building.getLevel()).willReturn(LEVEL);
        given(storedResourceAmountQueryService.getActualAmount(gameData, LOCATION, StorageType.BULK)).willReturn(TOTAL_CAPACITY - LEVEL * CAPACITY + 1);

        Throwable ex = catchThrowable(() -> underTest.checkIfBuildingCanBeDeconstructed(gameData, building));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.SKYXPLORE_STORAGE_USED);
    }
}