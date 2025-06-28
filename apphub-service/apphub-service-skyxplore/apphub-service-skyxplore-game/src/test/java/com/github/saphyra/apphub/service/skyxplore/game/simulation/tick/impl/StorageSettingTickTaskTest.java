package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting.StorageSettingProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting.StorageSettingProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StorageSettingTickTaskTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 1000;
    private static final Integer IN_PROGRESS_AMOUNT = 100;
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final Integer MISSING_AMOUNT = 40;
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final Integer AVAILABLE_STORAGE = 25;

    @Mock
    private StorageSettingProcessFactory storageSettingProcessFactory;

    @Mock
    private StorageCapacityService storageCapacityService;

    @Mock
    private BuildingModuleService buildingModuleService;

    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private StorageSettingTickTask underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private StorageSetting storageSetting;

    private final StorageSettings storageSettings = new StorageSettings();

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private Processes processes;

    @Mock
    private StorageSettingProcess storageSettingProcess;

    @Mock
    private ResourceData resourceData;

    @Mock
    private BuildingModule buildingModule;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.STORAGE_SETTING);
    }

    @Test
    void process_enoughInStorage() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        storageSettings.add(storageSetting);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(storedResources.getByLocationAndDataId(LOCATION, DATA_ID)).willReturn(List.of(storedResource));
        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT);
        given(storageSetting.getTargetAmount()).willReturn(TARGET_AMOUNT - 1);

        underTest.process(game);

        then(storageSettingProcessFactory).shouldHaveNoInteractions();
    }

    @Test
    void process_enoughProduced() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        storageSettings.add(storageSetting);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(storedResources.getByLocationAndDataId(LOCATION, DATA_ID)).willReturn(List.of(storedResource));
        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT - IN_PROGRESS_AMOUNT);
        given(storageSetting.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(gameData.getProcesses()).willReturn(processes);
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(processes.getByExternalReferenceAndType(STORAGE_SETTING_ID, ProcessType.STORAGE_SETTING)).willReturn(List.of(storageSettingProcess));
        given(storageSettingProcess.getAmount()).willReturn(IN_PROGRESS_AMOUNT + 1);

        underTest.process(game);

        then(storageSettingProcessFactory).shouldHaveNoInteractions();
    }

    @Test
    void process() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        storageSettings.add(storageSetting);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(storedResources.getByLocationAndDataId(LOCATION, DATA_ID)).willReturn(List.of(storedResource));
        given(storedResource.getAmount()).willReturn(TARGET_AMOUNT - IN_PROGRESS_AMOUNT);
        given(storageSetting.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(gameData.getProcesses()).willReturn(processes);
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(processes.getByExternalReferenceAndType(STORAGE_SETTING_ID, ProcessType.STORAGE_SETTING)).willReturn(List.of(storageSettingProcess));
        given(storageSettingProcess.getAmount()).willReturn(IN_PROGRESS_AMOUNT - MISSING_AMOUNT);
        given(resourceDataService.get(DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(buildingModuleService.getUsableDepots(gameData, LOCATION, StorageType.CONTAINER)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(storageCapacityService.getFreeContainerCapacity(gameData, BUILDING_MODULE_ID, StorageType.CONTAINER)).willReturn(AVAILABLE_STORAGE);

        underTest.process(game);

        then(storageSettingProcessFactory).should().save(game, storageSetting, BUILDING_MODULE_ID, AVAILABLE_STORAGE);
    }
}