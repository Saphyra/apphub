package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductionBuildingValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingDataValidator buildingDataValidator;

    @Mock
    private ProductionDataValidator productionDataValidator;

    @InjectMocks
    private ProductionBuildingValidator underTest;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private ProductionData productionData;

    @AfterEach
    public void validate() {
        verify(buildingDataValidator).validate(productionBuilding);
    }

    @Test
    public void nullWorkers() {
        given(productionBuilding.getWorkers()).willReturn(null);
        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void tooLowWorkers() {
        given(productionBuilding.getWorkers()).willReturn(0);
        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullPrimarySurfaceType() {
        given(productionBuilding.getWorkers()).willReturn(1);
        given(productionBuilding.getPrimarySurfaceType()).willReturn(null);
        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullPlaceableSurfaceTypes() {
        given(productionBuilding.getWorkers()).willReturn(1);
        given(productionBuilding.getPrimarySurfaceType()).willReturn(SurfaceType.DESERT);
        given(productionBuilding.getPlaceableSurfaceTypes()).willReturn(null);
        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void valid() {
        given(productionBuilding.getWorkers()).willReturn(1);
        given(productionBuilding.getPrimarySurfaceType()).willReturn(SurfaceType.DESERT);
        given(productionBuilding.getPlaceableSurfaceTypes()).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        OptionalHashMap<String, ProductionData> gives = new OptionalHashMap<>();
        gives.put(KEY, productionData);
        given(productionBuilding.getGives()).willReturn(gives);

        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        underTest.validate(map);

        verify(productionDataValidator).validate(gives);
    }
}