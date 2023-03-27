package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ResourceAllocationServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer REQUIRED_AMOUNT = 34;
    private static final Integer REQUIRED_STORAGE = 235;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FreeStorageQueryService freeStorageQueryService;

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceToModelConverter;

    @Mock
    private ReservedStorageToModelConverter reservedStorageToModelConverter;

    @Mock
    private ConsumptionCalculator consumptionCalculator;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private RequiredEmptyStorageCalculator requiredEmptyStorageCalculator;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @Mock
    private WsMessageSender messageSender;

    @InjectMocks
    private ResourceAllocationService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private ConsumptionResult consumptionResult;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private PlanetStorageResponse storageResponse;

    @BeforeEach
    public void setUp() {
        given(consumptionCalculator.calculate(gameData, PLANET_ID, EXTERNAL_REFERENCE, DATA_ID, REQUIRED_AMOUNT)).willReturn(consumptionResult);
        given(requiredEmptyStorageCalculator.getRequiredStorageAmount(StorageType.BULK, CollectionUtils.toMap(DATA_ID, consumptionResult))).willReturn(REQUIRED_STORAGE);
    }

    @Test
    public void notEnoughStorage() {
        given(freeStorageQueryService.getFreeStorage(gameData, PLANET_ID, StorageType.BULK)).willReturn(REQUIRED_STORAGE - 1);

        Throwable ex = catchThrowable(() -> underTest.processResourceRequirements(gameData, PLANET_ID, USER_ID, EXTERNAL_REFERENCE, CollectionUtils.toMap(DATA_ID, REQUIRED_AMOUNT)));

        ExceptionValidator.validateNotLoggedException(
            ex,
            HttpStatus.BAD_REQUEST,
            ErrorCode.NOT_ENOUGH_STORAGE,
            "storageType",
            StorageType.BULK.name()
        );
    }

    @Test
    public void processResourceRequirements() {
        given(freeStorageQueryService.getFreeStorage(gameData, PLANET_ID, StorageType.BULK)).willReturn(REQUIRED_STORAGE);
        given(consumptionResult.getAllocation()).willReturn(allocatedResource);
        given(consumptionResult.getReservation()).willReturn(reservedStorage);

        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResourceToModelConverter.convert(GAME_ID, allocatedResource)).willReturn(allocatedResourceModel);
        given(reservedStorageToModelConverter.convert(GAME_ID, reservedStorage)).willReturn(reservedStorageModel);
        given(planetStorageOverviewQueryService.getStorage(gameData, PLANET_ID)).willReturn(storageResponse);

        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        underTest.processResourceRequirements(gameData, PLANET_ID, USER_ID, EXTERNAL_REFERENCE, CollectionUtils.toMap(DATA_ID, REQUIRED_AMOUNT));

        verify(reservedStorages).add(reservedStorage);
        verify(allocatedResources).add(allocatedResource);
        verify(gameDataProxy).saveItems(List.of(allocatedResourceModel, reservedStorageModel));
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, storageResponse);
    }
}