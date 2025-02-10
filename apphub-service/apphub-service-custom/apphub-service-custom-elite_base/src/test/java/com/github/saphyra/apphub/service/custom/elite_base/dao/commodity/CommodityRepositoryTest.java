package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityEntityId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityRepository;
import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class CommodityRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String ID_3 = "id-3";
    private static final String ID_4 = "id-4";
    private static final String EXTERNAL_REFERENCE_1 = "external-reference-1";
    private static final String EXTERNAL_REFERENCE_2 = "external-reference-2";
    private static final Long MARKET_ID_1 = 1L;
    private static final Long MARKET_ID_2 = 2L;
    private static final String COMMODITY_NAME_1 = "commodity-name-1";
    private static final String COMMODITY_NAME_2 = "commodity-name-2";

    @Autowired
    private CommodityRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByExternalReferenceOrMarketId() {
        CommodityEntity entity1 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1)
                .commodityName(COMMODITY_NAME_1)
                .build())
            .marketId(MARKET_ID_1)
            .build();
        underTest.save(entity1);
        CommodityEntity entity2 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_2)
                .commodityName(COMMODITY_NAME_1)
                .build())
            .marketId(MARKET_ID_1)
            .build();
        underTest.save(entity2);
        CommodityEntity entity3 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1)
                .commodityName(COMMODITY_NAME_2)
                .build())
            .marketId(MARKET_ID_2)
            .build();
        underTest.save(entity3);
        CommodityEntity entity4 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_2)
                .commodityName(COMMODITY_NAME_2)
                .build())
            .marketId(MARKET_ID_2)
            .build();
        underTest.save(entity4);

        assertThat(underTest.getByIdExternalReferenceOrMarketId(EXTERNAL_REFERENCE_1, MARKET_ID_1)).containsExactlyInAnyOrder(entity1, entity2, entity3);
    }
}