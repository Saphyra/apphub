package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting.StorageSettingProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting.StorageSettingProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class StorageSettingTickTaskTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 425;
    private static final Integer PRODUCED_AMOUNT = 34;
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final Integer FREE_STORAGE = 23;
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final Integer MAX_BATCH_SIZE = FREE_STORAGE - 1;

    @Mock
    private StorageSettingProcessFactory storageSettingProcessFactory;

    @Mock
    private FreeStorageQueryService freeStorageQueryService;

    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private StorageSettingTickTask underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private Processes processes;

    @Mock
    private StorageSettingProcess storageSettingProcess;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Planet planet;

    @Mock
    private ResourceData resourceData;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.STORAGE_SETTING);
    }

    @Test
    void process_targetAmountReached() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(CollectionUtils.toList(new StorageSettings(), storageSetting));
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(LOCATION, DATA_ID)).willReturn(Optional.of(storedResource));
        given(storageSetting.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT);

        underTest.process(game, syncCache);

        verify(gameData, times(0)).getProcesses();
    }

    @Test
    void process_productionInProgress() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(CollectionUtils.toList(new StorageSettings(), storageSetting));
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(LOCATION, DATA_ID)).willReturn(Optional.of(storedResource));
        given(storageSetting.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT - PRODUCED_AMOUNT);
        given(gameData.getProcesses()).willReturn(processes);
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(processes.getByExternalReferenceAndType(STORAGE_SETTING_ID, ProcessType.STORAGE_SETTING)).willReturn(List.of(storageSettingProcess));
        given(storageSettingProcess.getAmount()).willReturn(PRODUCED_AMOUNT);

        underTest.process(game, syncCache);

        verify(freeStorageQueryService, times(0)).getFreeStorage(gameData, LOCATION, DATA_ID);
    }

    @Test
    void process_storageFull() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(CollectionUtils.toList(new StorageSettings(), storageSetting));
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(LOCATION, DATA_ID)).willReturn(Optional.of(storedResource));
        given(storageSetting.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(storedResource.getAmount()).willReturn(0);
        given(gameData.getProcesses()).willReturn(processes);
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(processes.getByExternalReferenceAndType(STORAGE_SETTING_ID, ProcessType.STORAGE_SETTING)).willReturn(Collections.emptyList());
        given(freeStorageQueryService.getFreeStorage(gameData, LOCATION, DATA_ID)).willReturn(0);

        underTest.process(game, syncCache);

        verifyNoInteractions(storageSettingProcessFactory);
    }

    @Test
    void process_create() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(CollectionUtils.toList(new StorageSettings(), storageSetting));
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(LOCATION, DATA_ID)).willReturn(Optional.of(storedResource));
        given(storageSetting.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(storedResource.getAmount()).willReturn(0);
        given(gameData.getProcesses()).willReturn(processes);
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(processes.getByExternalReferenceAndType(STORAGE_SETTING_ID, ProcessType.STORAGE_SETTING)).willReturn(Collections.emptyList());
        given(freeStorageQueryService.getFreeStorage(gameData, LOCATION, DATA_ID)).willReturn(FREE_STORAGE);
        given(storageSettingProcessFactory.create(gameData, storageSetting, MAX_BATCH_SIZE)).willReturn(storageSettingProcess);
        given(storageSettingProcess.toModel()).willReturn(processModel);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);
        given(resourceDataService.get(DATA_ID)).willReturn(resourceData);
        given(resourceData.getMaxProductionBatchSize()).willReturn(MAX_BATCH_SIZE);

        underTest.process(game, syncCache);

        verify(processes).add(storageSettingProcess);
        verify(syncCache).saveGameItem(processModel);
        verify(syncCache).storageModified(OWNER_ID, LOCATION);
    }
}