package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class StorageCalculatorTest {
    private static final String STORAGE_BUILDING_DATA_ID = "storage-building-data-id";
    private static final Integer CAPACITY = 314;
    private static final Integer LEVEL = 235;

    @Mock
    private StorageBuildingService storageBuildingService;

    @InjectMocks
    private StorageCalculator underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface emptySurface;

    @Mock
    private Surface notStorageSurface;

    @Mock
    private Surface storageSurface;

    @Mock
    private Building notStorageBuilding;

    @Mock
    private Building storageBuilding;

    @Mock
    private StorageBuilding storageBuildingData;

    @Test
    public void calculateCapacity() {
        given(planet.getSurfaces()).willReturn(new SurfaceMap(
            CollectionUtils.toMap(
                new BiWrapper<>(new Coordinate(0, 0), emptySurface),
                new BiWrapper<>(new Coordinate(0, 1), notStorageSurface),
                new BiWrapper<>(new Coordinate(0, 2), storageSurface)
            ))
        );

        given(notStorageSurface.getBuilding()).willReturn(notStorageBuilding);
        given(storageSurface.getBuilding()).willReturn(storageBuilding);

        given(notStorageBuilding.getDataId()).willReturn("asd");
        given(storageBuilding.getDataId()).willReturn(STORAGE_BUILDING_DATA_ID);

        given(storageBuildingService.findByStorageType(StorageType.CITIZEN)).willReturn(storageBuildingData);
        given(storageBuildingData.getId()).willReturn(STORAGE_BUILDING_DATA_ID);
        given(storageBuildingData.getCapacity()).willReturn(CAPACITY);

        given(storageBuilding.getLevel()).willReturn(LEVEL);

        int result = underTest.calculateCapacity(planet, StorageType.CITIZEN);

        assertThat(result).isEqualTo(LEVEL * CAPACITY);
    }
}