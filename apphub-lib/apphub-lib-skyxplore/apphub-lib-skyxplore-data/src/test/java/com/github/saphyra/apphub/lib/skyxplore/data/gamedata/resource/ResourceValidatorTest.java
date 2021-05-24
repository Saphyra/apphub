package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.GameDataItemValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ResourceValidatorTest {
    private static final String KEY = "key";
    private static final String ID = "id";

    @Mock
    private GameDataItemValidator gameDataItemValidator;

    @Mock
    private ProductionBuildingService productionBuildingService;

    @InjectMocks
    private ResourceValidator underTest;

    @Mock
    private ResourceData resourceData;

    @Mock
    private ProductionData productionData;

    @Mock
    private ProductionBuilding productionBuilding;

    @After
    public void validate() {
        verify(gameDataItemValidator).validate(resourceData);
    }

    @Test(expected = IllegalStateException.class)
    public void nullStorageType() {
        Map<String, ResourceData> map = new HashMap<>();
        map.put(KEY, resourceData);
        given(resourceData.getStorageType()).willReturn(null);

        underTest.validate(map);
    }

    @Test(expected = IllegalStateException.class)
    public void noProducer() {
        Map<String, ResourceData> map = new HashMap<>();
        map.put(KEY, resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CITIZEN);
        given(productionBuildingService.values()).willReturn(Collections.emptyList());

        underTest.validate(map);
    }

    @Test
    public void valid() {
        Map<String, ResourceData> map = new HashMap<>();
        map.put(KEY, resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CITIZEN);
        given(productionBuildingService.values()).willReturn(Arrays.asList(productionBuilding));

        Map<String, ProductionData> productionDataMap = new HashMap<>();
        productionDataMap.put(ID, productionData);
        given(productionBuilding.getGives()).willReturn(new OptionalHashMap<>(productionDataMap));
        given(resourceData.getId()).willReturn(ID);

        underTest.validate(map);
    }
}