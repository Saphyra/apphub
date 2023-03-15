package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AvailableResourceCounter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductionRequirementsAllocationServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 3214;
    private static final Integer AVAILABLE_AMOUNT = 123;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private AvailableResourceCounter availableResourceCounter;

    @Mock
    private AllocatedResourceFactory allocatedResourceFactory;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceToModelConverter;

    @Mock
    private ReservedStorageToModelConverter reservedStorageToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @InjectMocks
    private ProductionRequirementsAllocationService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

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
    private PlanetStorageResponse planetStorageResponse;

    @Test
    public void allocate() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(availableResourceCounter.countAvailableAmount(storageDetails, DATA_ID)).willReturn(AVAILABLE_AMOUNT);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(allocatedResourceFactory.create(PLANET_ID, LocationType.PRODUCTION, EXTERNAL_REFERENCE, DATA_ID, AVAILABLE_AMOUNT)).willReturn(allocatedResource);
        given(reservedStorageFactory.create(PLANET_ID, LocationType.PRODUCTION, EXTERNAL_REFERENCE, DATA_ID, AMOUNT - AVAILABLE_AMOUNT)).willReturn(reservedStorage);
        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(allocatedResourceToModelConverter.convert(allocatedResource, GAME_ID)).willReturn(allocatedResourceModel);
        given(reservedStorageToModelConverter.convert(reservedStorage, GAME_ID)).willReturn(reservedStorageModel);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planetStorageOverviewQueryService.getStorage(planet)).willReturn(planetStorageResponse);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        UUID result = underTest.allocate(syncCache, GAME_ID, planet, EXTERNAL_REFERENCE, DATA_ID, AMOUNT);

        verify(allocatedResources).add(allocatedResource);
        verify(reservedStorages).add(reservedStorage);
        verify(syncCache).saveGameItem(allocatedResourceModel);
        verify(syncCache).saveGameItem(reservedStorageModel);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(PLANET_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetStorageModified(USER_ID, PLANET_ID, planetStorageResponse);

        assertThat(result).isEqualTo(RESERVED_STORAGE_ID);
    }
}