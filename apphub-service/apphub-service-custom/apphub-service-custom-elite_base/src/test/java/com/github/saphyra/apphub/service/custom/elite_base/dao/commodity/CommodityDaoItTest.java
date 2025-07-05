package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CommodityDaoItTest {
    private static final UUID EXTERNAL_REFERENCE_1 = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE_2 = UUID.randomUUID();
    private static final String COMMODITY_NAME_1 = "commodity-name-1";
    private static final String COMMODITY_NAME_2 = "commodity-name-2";

    @Autowired
    private CommodityDao underTest;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

    @AfterEach
    void clear(){
        underTest.deleteAll();
    }

    @Test
    void getCommodityNames() {
        Commodity commodity1 = Commodity.builder()
            .externalReference(EXTERNAL_REFERENCE_1)
            .commodityName(COMMODITY_NAME_1)
            .type(CommodityType.COMMODITY)
            .build();
        Commodity commodity2 = Commodity.builder()
            .externalReference(EXTERNAL_REFERENCE_2)
            .commodityName(COMMODITY_NAME_2)
            .type(CommodityType.COMMODITY)
            .build();
        underTest.saveAll(List.of(commodity1, commodity2));
        bufferSynchronizationService.synchronizeAll();

        assertThat(underTest.getCommodityNames()).containsExactlyInAnyOrder(COMMODITY_NAME_1, COMMODITY_NAME_2);
    }
}