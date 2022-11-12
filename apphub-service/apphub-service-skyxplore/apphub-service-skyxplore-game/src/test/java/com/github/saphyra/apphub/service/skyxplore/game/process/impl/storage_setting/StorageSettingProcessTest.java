package com.github.saphyra.apphub.service.skyxplore.game.process.impl.storage_setting;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingProcessTest {
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final Integer PLANET_PRIORITY = 423;
    private static final Integer STORAGE_SETTING_PRIORITY = 36;
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 2134;
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    public static final int MISSING_AMOUNT = 10;
    private static final Integer FREE_STORAGE = 6;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private FreeStorageQueryService freeStorageQueryService;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @Mock
    private ReservedStorageToModelConverter reservedStorageToModelConverter;

    @Mock
    private WsMessageSender wsMessageSender;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @Mock
    private ProductionOrderProcessFactory productionOrderProcessFactory;

    private StorageSettingProcess underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedReservedStorage;

    @Mock
    private Processes processes;

    @Mock
    private ProductionOrderProcess finishedProcess;

    @Mock
    private ResourceData resourceData;

    @Mock
    private ReservedStorage createdReservedStorage;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Mock
    private ProcessModel processModel;

    @Before
    public void setUp() {
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(storageSetting.getTargetAmount()).willReturn(TARGET_AMOUNT);

        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStoredResources()).willReturn(storedResources);
        given(storedResources.get(DATA_ID)).willReturn(storedResource);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.getByExternalReference(PROCESS_ID)).willReturn(List.of(reservedReservedStorage));
        given(reservedReservedStorage.getAmount()).willReturn(0);
        given(reservedReservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        given(game.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(finishedProcess));
        given(finishedProcess.getStatus()).willReturn(ProcessStatus.DONE);

        underTest = StorageSettingProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.IN_PROGRESS)
            .game(game)
            .planet(planet)
            .storageSetting(storageSetting)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    public void getExternalReference() {
        assertThat(underTest.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
    }

    @Test
    public void getPriority() {
        given(planet.getPriorities()).willReturn(CollectionUtils.singleValueMap(PriorityType.MANUFACTURING, PLANET_PRIORITY));
        given(storageSetting.getPriority()).willReturn(STORAGE_SETTING_PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PLANET_PRIORITY * STORAGE_SETTING_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.STORAGE_SETTING);
    }

    @Test
    public void work_enoughInStorage() {
        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT);

        underTest.work(syncCache);

        verifyCleanup();
    }

    @Test
    public void work_missingAlreadyInProgress() {
        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT - MISSING_AMOUNT);
        given(finishedProcess.getAmount()).willReturn(MISSING_AMOUNT);

        underTest.work(syncCache);

        verifyCleanup();
    }

    @Test
    public void work_noAvailableStorage() {
        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT - MISSING_AMOUNT);
        given(finishedProcess.getAmount()).willReturn(0);

        given(applicationContextProxy.getBean(ResourceDataService.class)).willReturn(resourceDataService);
        given(resourceDataService.get(DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.LIQUID);
        given(applicationContextProxy.getBean(FreeStorageQueryService.class)).willReturn(freeStorageQueryService);
        given(freeStorageQueryService.getFreeStorage(planet, StorageType.LIQUID)).willReturn(0);

        underTest.work(syncCache);

        verifyCleanup();
    }

    @Test
    public void work_initiateProduction() {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(game.getGameId()).willReturn(GAME_ID);
        given(planet.getOwner()).willReturn(USER_ID);

        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT - MISSING_AMOUNT);
        given(finishedProcess.getAmount()).willReturn(0);

        given(applicationContextProxy.getBean(ResourceDataService.class)).willReturn(resourceDataService);
        given(resourceDataService.get(DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.LIQUID);
        given(applicationContextProxy.getBean(FreeStorageQueryService.class)).willReturn(freeStorageQueryService);
        given(freeStorageQueryService.getFreeStorage(planet, StorageType.LIQUID)).willReturn(FREE_STORAGE);

        given(applicationContextProxy.getBean(ReservedStorageFactory.class)).willReturn(reservedStorageFactory);
        given(reservedStorageFactory.create(PLANET_ID, LocationType.PLANET, PROCESS_ID, DATA_ID, FREE_STORAGE)).willReturn(createdReservedStorage);
        given(applicationContextProxy.getBean(ReservedStorageToModelConverter.class)).willReturn(reservedStorageToModelConverter);
        given(reservedStorageToModelConverter.convert(createdReservedStorage, GAME_ID)).willReturn(reservedStorageModel);

        given(applicationContextProxy.getBean(WsMessageSender.class)).willReturn(wsMessageSender);
        given(applicationContextProxy.getBean(PlanetStorageOverviewQueryService.class)).willReturn(planetStorageOverviewQueryService);
        given(planetStorageOverviewQueryService.getStorage(planet)).willReturn(planetStorageResponse);

        given(applicationContextProxy.getBean(ProductionOrderProcessFactory.class)).willReturn(productionOrderProcessFactory);
        given(productionOrderProcessFactory.create(PROCESS_ID, game, planet, createdReservedStorage)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.toModel()).willReturn(processModel);

        underTest.work(syncCache);

        verify(reservedStorages).add(createdReservedStorage);
        verify(syncCache).saveGameItem(reservedStorageModel);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED), eq(PLANET_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(wsMessageSender).planetStorageModified(USER_ID, PLANET_ID, planetStorageResponse);

        verify(processes).add(productionOrderProcess);
        verify(syncCache).saveGameItem(processModel);

        verifyCleanup();
    }

    private void verifyCleanup() {
        verify(reservedStorages).removeIf(any());

        verify(syncCache).deleteGameItem(RESERVED_STORAGE_ID, GameItemType.RESERVED_STORAGE);

        verify(finishedProcess).cleanup(syncCache);
    }
}