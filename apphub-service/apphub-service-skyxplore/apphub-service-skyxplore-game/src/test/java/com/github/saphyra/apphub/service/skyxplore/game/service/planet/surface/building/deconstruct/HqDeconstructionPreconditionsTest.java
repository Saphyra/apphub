package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HqDeconstructionPreconditionsTest {
    private static final Integer CAPACITY = 24;
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer LEVEL = 2;
    private static final Integer STORED_AMOUNT = 423;

    @Mock
    private HeadquartersUtil headquartersUtil;

    @Mock
    private StorageCalculator storageCalculator;

    @Mock
    private StoredResourceAmountQueryService storedResourceAmountQueryService;

    @InjectMocks
    private HqDeconstructionPreconditions underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Building building;

    @Test
    void hasEnoughCapacityLeft() {
        given(headquartersUtil.getStores()).willReturn(Map.of(StorageType.CONTAINER, CAPACITY));
        given(building.getLocation()).willReturn(LOCATION);
        given(building.getLevel()).willReturn(LEVEL);
        given(storageCalculator.calculateCapacity(gameData, LOCATION, StorageType.CONTAINER)).willReturn(CAPACITY * 2 + STORED_AMOUNT + 1);
        given(storedResourceAmountQueryService.getActualAmount(gameData, LOCATION, StorageType.CONTAINER)).willReturn(STORED_AMOUNT);

        underTest.checkHqCanBeDeconstructed(gameData, building);
    }

    @Test
    void notEnoughCapacityLeft(){
        given(headquartersUtil.getStores()).willReturn(Map.of(StorageType.CONTAINER, CAPACITY));
        given(building.getLocation()).willReturn(LOCATION);
        given(building.getLevel()).willReturn(LEVEL);
        given(storageCalculator.calculateCapacity(gameData, LOCATION, StorageType.CONTAINER)).willReturn(CAPACITY * 2 + STORED_AMOUNT - 1);
        given(storedResourceAmountQueryService.getActualAmount(gameData, LOCATION, StorageType.CONTAINER)).willReturn(STORED_AMOUNT);

        Throwable ex = catchThrowable(()-> underTest.checkHqCanBeDeconstructed(gameData, building));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.SKYXPLORE_STORAGE_USED);
    }
}