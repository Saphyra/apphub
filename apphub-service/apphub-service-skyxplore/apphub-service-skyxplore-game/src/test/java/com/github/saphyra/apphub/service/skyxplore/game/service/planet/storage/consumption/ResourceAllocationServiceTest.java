package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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
    private StorageDetails storageDetails;

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

    @Before
    public void setUp() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(consumptionCalculator.calculate(planet, LocationType.PLANET, EXTERNAL_REFERENCE, DATA_ID, REQUIRED_AMOUNT)).willReturn(consumptionResult);
        given(requiredEmptyStorageCalculator.getRequiredStorageAmount(StorageType.BULK, CollectionUtils.singleValueMap(DATA_ID, consumptionResult))).willReturn(REQUIRED_STORAGE);
    }

    @Test
    public void notEnoughStorage() {
        given(freeStorageQueryService.getFreeStorage(planet, StorageType.BULK)).willReturn(REQUIRED_STORAGE - 1);

        Throwable ex = catchThrowable(() -> underTest.processResourceRequirements(GAME_ID, planet, LocationType.PLANET, EXTERNAL_REFERENCE, CollectionUtils.singleValueMap(DATA_ID, REQUIRED_AMOUNT)));

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
        given(freeStorageQueryService.getFreeStorage(planet, StorageType.BULK)).willReturn(REQUIRED_STORAGE);
        given(consumptionResult.getAllocation()).willReturn(allocatedResource);
        given(consumptionResult.getReservation()).willReturn(reservedStorage);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResourceToModelConverter.convert(allocatedResource, GAME_ID)).willReturn(allocatedResourceModel);
        given(reservedStorageToModelConverter.convert(reservedStorage, GAME_ID)).willReturn(reservedStorageModel);
        given(planetStorageOverviewQueryService.getStorage(planet)).willReturn(storageResponse);

        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        underTest.processResourceRequirements(GAME_ID, planet, LocationType.PLANET, EXTERNAL_REFERENCE, CollectionUtils.singleValueMap(DATA_ID, REQUIRED_AMOUNT));

        verify(reservedStorages).add(reservedStorage);
        verify(allocatedResources).add(allocatedResource);
        verify(gameDataProxy).saveItems(List.of(allocatedResourceModel, reservedStorageModel));
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, storageResponse);
    }
}