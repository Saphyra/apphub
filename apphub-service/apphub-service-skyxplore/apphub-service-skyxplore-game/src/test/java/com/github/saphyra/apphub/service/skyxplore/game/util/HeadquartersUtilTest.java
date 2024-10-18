package com.github.saphyra.apphub.service.skyxplore.game.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous.MiscellaneousBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous.MiscellaneousBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HeadquartersUtilTest {
    private static final String DATA_ID = "data-id";
    private static final int WORKERS = 24;
    private static final Integer CAPACITY = 53;

    @Mock
    private MiscellaneousBuildingService miscellaneousBuildingService;

    @Spy
    private ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(new ObjectMapper());

    @InjectMocks
    private HeadquartersUtil underTest;

    @Mock
    private MiscellaneousBuilding miscellaneousBuilding;

    @BeforeEach
    void setUp() {
        given(miscellaneousBuildingService.get(GameConstants.DATA_ID_HEADQUARTERS)).willReturn(miscellaneousBuilding);
    }

    @Test
    void getGives() {
        given(miscellaneousBuilding.getData()).willReturn(Map.of("gives", Map.of(DATA_ID, new ProductionData())));

        assertThat(underTest.getGives()).containsExactly(DATA_ID);
    }

    @Test
    void getProductionData() {
        ProductionData productionData = ProductionData.builder()
            .build();
        given(miscellaneousBuilding.getData()).willReturn(Map.of("gives", Map.of(DATA_ID, productionData)));

        assertThat(underTest.getProductionData(DATA_ID)).isEqualTo(productionData);
    }

    @Test
    void getWorkers() {
        given(miscellaneousBuilding.getData()).willReturn(Map.of("workers", WORKERS));

        assertThat(underTest.getWorkers()).isEqualTo(WORKERS);
    }

    @Test
    void getStores() {
        given(miscellaneousBuilding.getData()).willReturn(Map.of("stores", Map.of(StorageType.CONTAINER, CAPACITY)));

        assertThat(underTest.getStores()).containsEntry(StorageType.CONTAINER, CAPACITY);
    }
}