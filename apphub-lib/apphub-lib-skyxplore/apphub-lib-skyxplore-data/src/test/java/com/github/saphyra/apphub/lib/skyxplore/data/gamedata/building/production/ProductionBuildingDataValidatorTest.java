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
public class ProductionBuildingDataValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingDataValidator buildingDataValidator;

    @Mock
    private ProductionDataValidator productionDataValidator;

    @InjectMocks
    private ProductionBuildingValidator underTest;

    @Mock
    private ProductionBuildingData productionBuildingData;

    @Mock
    private ProductionData productionData;

    @AfterEach
    public void validate() {
        verify(buildingDataValidator).validate(productionBuildingData);
    }

    @Test
    public void nullWorkers() {
        given(productionBuildingData.getWorkers()).willReturn(null);
        Map<String, ProductionBuildingData> map = new HashMap<>();
        map.put(KEY, productionBuildingData);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void tooLowWorkers() {
        given(productionBuildingData.getWorkers()).willReturn(0);
        Map<String, ProductionBuildingData> map = new HashMap<>();
        map.put(KEY, productionBuildingData);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullPrimarySurfaceType() {
        given(productionBuildingData.getWorkers()).willReturn(1);
        given(productionBuildingData.getPrimarySurfaceType()).willReturn(null);
        Map<String, ProductionBuildingData> map = new HashMap<>();
        map.put(KEY, productionBuildingData);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullPlaceableSurfaceTypes() {
        given(productionBuildingData.getWorkers()).willReturn(1);
        given(productionBuildingData.getPrimarySurfaceType()).willReturn(SurfaceType.DESERT);
        given(productionBuildingData.getPlaceableSurfaceTypes()).willReturn(null);
        Map<String, ProductionBuildingData> map = new HashMap<>();
        map.put(KEY, productionBuildingData);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void valid() {
        given(productionBuildingData.getWorkers()).willReturn(1);
        given(productionBuildingData.getPrimarySurfaceType()).willReturn(SurfaceType.DESERT);
        given(productionBuildingData.getPlaceableSurfaceTypes()).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        OptionalHashMap<String, ProductionData> gives = new OptionalHashMap<>();
        gives.put(KEY, productionData);
        given(productionBuildingData.getGives()).willReturn(gives);

        Map<String, ProductionBuildingData> map = new HashMap<>();
        map.put(KEY, productionBuildingData);

        underTest.validate(map);

        verify(productionDataValidator).validate(gives);
    }
}