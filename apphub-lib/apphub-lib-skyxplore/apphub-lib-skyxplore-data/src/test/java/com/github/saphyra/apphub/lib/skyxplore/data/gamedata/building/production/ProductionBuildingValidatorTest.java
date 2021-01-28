package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.common_util.map.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataValidator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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

    @After
    public void validate() {
        verify(buildingDataValidator).validate(productionBuilding);
    }

    @Test(expected = IllegalStateException.class)
    public void nullWorkers() {
        given(productionBuilding.getWorkers()).willReturn(null);
        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void tooLowWorkers() {
        given(productionBuilding.getWorkers()).willReturn(0);
        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void nullPrimarySurfaceType() {
        given(productionBuilding.getWorkers()).willReturn(1);
        given(productionBuilding.getPrimarySurfaceType()).willReturn(null);
        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void nullPlaceableSurfaceTypes() {
        given(productionBuilding.getWorkers()).willReturn(1);
        given(productionBuilding.getPrimarySurfaceType()).willReturn(SurfaceType.DESERT);
        given(productionBuilding.getPlaceableSurfaceTypes()).willReturn(null);
        Map<String, ProductionBuilding> map = new HashMap<>();
        map.put(KEY, productionBuilding);

        underTest.validate(map);
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