package com.github.saphyra.apphub.service.skyxplore.game.service.logistics;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CoordinateFinderTest {
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @InjectMocks
    private CoordinateFinder underTest;

    @Mock
    private GameData gameData;

    @Mock
    private StoredResource storedResource;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private Coordinates coordinates;

    @Mock
    private Coordinate coordinate;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Test
    void getCoordinateByStoredResource_storage() {
        given(storedResource.getContainerId()).willReturn(BUILDING_MODULE_ID);
        given(storedResource.getContainerType()).willReturn(ContainerType.STORAGE);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.findByIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);
        given(buildingModule.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getSurfaceId()).willReturn(SURFACE_ID);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceIdValidated(SURFACE_ID)).willReturn(coordinate);

        assertThat(underTest.getCoordinateByStoredResource(gameData, storedResource)).isEqualTo(coordinate);
    }

    @Test
    void getCoordinateByStoredResource_constructionArea() {
        given(storedResource.getContainerId()).willReturn(CONSTRUCTION_AREA_ID);
        given(storedResource.getContainerType()).willReturn(ContainerType.CONSTRUCTION_AREA);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getSurfaceId()).willReturn(SURFACE_ID);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceIdValidated(SURFACE_ID)).willReturn(coordinate);

        assertThat(underTest.getCoordinateByStoredResource(gameData, storedResource)).isEqualTo(coordinate);
    }

    @Test
    void getCoordinateByStoredResource_surface() {
        given(storedResource.getContainerId()).willReturn(SURFACE_ID);
        given(storedResource.getContainerType()).willReturn(ContainerType.SURFACE);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceIdValidated(SURFACE_ID)).willReturn(coordinate);

        assertThat(underTest.getCoordinateByStoredResource(gameData, storedResource)).isEqualTo(coordinate);
    }

    @Test
    void getCoordinateByReservedStorageId() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getContainerId()).willReturn(SURFACE_ID);
        given(reservedStorage.getContainerType()).willReturn(ContainerType.SURFACE);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceIdValidated(SURFACE_ID)).willReturn(coordinate);

        assertThat(underTest.getCoordinateByReservedStorageId(gameData, RESERVED_STORAGE_ID)).isEqualTo(coordinate);
    }
}