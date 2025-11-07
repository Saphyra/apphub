package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CommodityDeleteBufferItTest {
    private static final UUID EXTERNAL_REFERENCE_1 = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE_2 = UUID.randomUUID();
    private static final String COMMODITY_NAME_1 = "commodity-name-1";
    private static final String COMMODITY_NAME_2 = "commodity-name-2";

    @Autowired
    private CommodityDeleteBuffer underTest;

    @Autowired
    private CommodityRepository commodityRepository;

    @AfterEach
    void clear() {
        commodityRepository.deleteAll();
    }

    @Test
    void synchronize() {
        CommodityDomainId commodityDomainId = CommodityDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE_1)
            .commodityName(COMMODITY_NAME_1)
            .build();
        underTest.add(commodityDomainId);

        CommodityEntity entity1 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1.toString())
                .commodityName(COMMODITY_NAME_1)
                .build())
            .build();
        commodityRepository.save(entity1);
        CommodityEntity entity2 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_2.toString())
                .commodityName(COMMODITY_NAME_1)
                .build())
            .build();
        commodityRepository.save(entity2);
        CommodityEntity entity3 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1.toString())
                .commodityName(COMMODITY_NAME_2)
                .build())
            .build();
        commodityRepository.save(entity3);

        underTest.synchronize();

        assertThat(commodityRepository.findAll()).containsExactlyInAnyOrder(entity2, entity3);
    }
}