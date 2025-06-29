package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StoredResourceServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final Integer AMOUNT = 3;
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_BY = UUID.randomUUID();

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @Mock
    private BuildingModuleService buildingModuleService;

    @Mock
    private StoredResourceAggregator storedResourceAggregator;

    @InjectMocks
    private StoredResourceService underTest;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private GameData gameData;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Mock
    private StoredResource storedResource3;

    @Mock
    private StoredResource storedResource4;

    @Mock
    private GameProgressDiff progressDiff;

    @Test
    void prepareBatch_notEnoughInBatch() {
        given(buildingModuleService.getDepots(gameData, LOCATION)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(BUILDING_MODULE_ID)).willReturn(List.of(storedResource1));
        given(storedResource1.getAllocatedBy()).willReturn(null);
        given(storedResource1.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResource1.getContainerId()).willReturn(BUILDING_MODULE_ID);
        given(storedResourceAggregator.aggregate(progressDiff, gameData, Map.of(BUILDING_MODULE_ID, List.of(storedResource1)))).willReturn(List.of(storedResource2));
        given(storedResource2.getAmount()).willReturn(AMOUNT - 1);

        assertThat(underTest.prepareBatch(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, AMOUNT)).containsExactly(storedResource2);
    }

    @Test
    void prepareBatch_moreStored() {
        given(buildingModuleService.getDepots(gameData, LOCATION)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(BUILDING_MODULE_ID)).willReturn(List.of(storedResource1));
        given(storedResource1.getAllocatedBy()).willReturn(null);
        given(storedResource1.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResource1.getContainerId()).willReturn(BUILDING_MODULE_ID);
        given(storedResourceAggregator.aggregate(progressDiff, gameData, Map.of(BUILDING_MODULE_ID, List.of(storedResource1)))).willReturn(List.of(storedResource2));
        given(storedResource2.getAmount()).willReturn(AMOUNT + 2);
        given(storedResource2.getLocation()).willReturn(LOCATION);
        given(storedResource2.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResource2.getContainerId()).willReturn(BUILDING_MODULE_ID);
        given(storedResource2.getContainerType()).willReturn(ContainerType.STORAGE);
        given(storedResource2.getStoredResourceId()).willReturn(STORED_RESOURCE_ID);
        given(storedResourceFactory.save(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, AMOUNT, BUILDING_MODULE_ID, ContainerType.STORAGE)).willReturn(storedResource4);
        given(storedResourceFactory.save(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, 2, BUILDING_MODULE_ID, ContainerType.STORAGE)).willReturn(mock(StoredResource.class));

        assertThat(underTest.prepareBatch(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, AMOUNT)).containsExactly(storedResource4);

        then(storedResourceFactory).should().save(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, 2, BUILDING_MODULE_ID, ContainerType.STORAGE);
        then(storedResources).should().remove(storedResource2);
        then(progressDiff).should().delete(STORED_RESOURCE_ID, GameItemType.STORED_RESOURCE);
    }

    @Test
    void count() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByAllocatedBy(ALLOCATED_BY)).willReturn(List.of(storedResource1));
        given(storedResource1.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResource1.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.count(gameData, RESOURCE_DATA_ID, ALLOCATED_BY)).isEqualTo(AMOUNT);
    }

    @Test
    void useResources() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByAllocatedBy(ALLOCATED_BY)).willReturn(List.of(storedResource1));
        given(storedResource1.getStoredResourceId()).willReturn(STORED_RESOURCE_ID);

        underTest.useResources(progressDiff, gameData, ALLOCATED_BY);

        then(storedResources).should().remove(storedResource1);
        then(progressDiff).should().delete(STORED_RESOURCE_ID, GameItemType.STORED_RESOURCE);
    }
}