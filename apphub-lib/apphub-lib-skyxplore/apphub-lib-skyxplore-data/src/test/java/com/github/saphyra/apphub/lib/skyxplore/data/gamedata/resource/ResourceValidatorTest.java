package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.GameDataItemValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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


    @AfterEach
    public void validate() {
        verify(gameDataItemValidator).validate(resourceData);
    }

    @Test
    public void nullStorageType() {
        given(resourceData.getStorageType()).willReturn(StorageType.CITIZEN);
        Map<String, ResourceData> map = new HashMap<>();
        map.put(KEY, resourceData);
        given(resourceData.getStorageType()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullMaxBatchSize() {
        given(resourceData.getMass()).willReturn(32);
        given(resourceData.getStorageType()).willReturn(StorageType.CITIZEN);
        Map<String, ResourceData> map = new HashMap<>();
        map.put(KEY, resourceData);
        given(resourceData.getMaxProductionBatchSize()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullMass() {
        given(resourceData.getMass()).willReturn(32);
        given(resourceData.getStorageType()).willReturn(StorageType.CITIZEN);
        Map<String, ResourceData> map = new HashMap<>();
        map.put(KEY, resourceData);
        given(resourceData.getMass()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void noProducer() {
        given(resourceData.getMass()).willReturn(32);
        given(resourceData.getStorageType()).willReturn(StorageType.CITIZEN);
        Map<String, ResourceData> map = new HashMap<>();
        map.put(KEY, resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CITIZEN);
        given(productionBuildingService.values()).willReturn(Collections.emptyList());

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void valid() {
        given(resourceData.getMass()).willReturn(32);
        given(resourceData.getStorageType()).willReturn(StorageType.CITIZEN);
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