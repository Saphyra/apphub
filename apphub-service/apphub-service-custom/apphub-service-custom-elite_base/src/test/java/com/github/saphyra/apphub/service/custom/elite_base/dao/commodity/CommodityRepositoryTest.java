package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class CommodityRepositoryTest {
    private static final String EXTERNAL_REFERENCE_1 = "external-reference-1";
    private static final String EXTERNAL_REFERENCE_2 = "external-reference-2";
    private static final String EXTERNAL_REFERENCE_3 = "external-reference-3";
    private static final String EXTERNAL_REFERENCE_4 = "external-reference-4";
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
    void getByMarketIdAndType() {
        CommodityEntity entity1 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1)
                .commodityName(COMMODITY_NAME_1)
                .build())
            .marketId(MARKET_ID_1)
            .type(CommodityType.COMMODITY)
            .build();
        underTest.save(entity1);
        CommodityEntity entity2 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_2)
                .commodityName(COMMODITY_NAME_1)
                .build())
            .marketId(MARKET_ID_1)
            .type(CommodityType.FC_MATERIAL)
            .build();
        underTest.save(entity2);
        CommodityEntity entity3 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1)
                .commodityName(COMMODITY_NAME_2)
                .build())
            .marketId(MARKET_ID_2)
            .type(CommodityType.COMMODITY)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByMarketIdAndType(MARKET_ID_1, CommodityType.COMMODITY)).containsExactlyInAnyOrder(entity1);
    }

    @Test
    void getSellOffers() {
        CommodityEntity matchingEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_1)
                .build())
            .stock(5)
            .sellPrice(10)
            .build();
        underTest.save(matchingEntity);
        CommodityEntity differentCommodityEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_2)
                .externalReference(EXTERNAL_REFERENCE_1)
                .build())
            .stock(5)
            .sellPrice(10)
            .build();
        underTest.save(differentCommodityEntity);
        CommodityEntity notEnoughDemandEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_2)
                .build())
            .stock(4)
            .sellPrice(10)
            .build();
        underTest.save(notEnoughDemandEntity);
        CommodityEntity tooCheapEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_3)
                .build())
            .stock(5)
            .sellPrice(9)
            .build();
        underTest.save(tooCheapEntity);
        CommodityEntity tooExpensiveEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_4)
                .build())
            .stock(5)
            .sellPrice(12)
            .build();
        underTest.save(tooExpensiveEntity);

        assertThat(underTest.getSellOffers(COMMODITY_NAME_1, 5, 10, 11)).containsExactly(matchingEntity);
    }

    @Test
    void getBuyOffers() {
        CommodityEntity matchingEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_1)
                .build())
            .demand(5)
            .buyPrice(10)
            .build();
        underTest.save(matchingEntity);
        CommodityEntity differentCommodityEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_2)
                .externalReference(EXTERNAL_REFERENCE_1)
                .build())
            .demand(5)
            .buyPrice(10)
            .build();
        underTest.save(differentCommodityEntity);
        CommodityEntity notEnoughDemandEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_2)
                .build())
            .demand(4)
            .buyPrice(10)
            .build();
        underTest.save(notEnoughDemandEntity);
        CommodityEntity tooCheapEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_3)
                .build())
            .demand(5)
            .buyPrice(9)
            .build();
        underTest.save(tooCheapEntity);
        CommodityEntity tooExpensiveEntity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_4)
                .build())
            .demand(5)
            .buyPrice(12)
            .build();
        underTest.save(tooExpensiveEntity);

        assertThat(underTest.getBuyOffers(COMMODITY_NAME_1, 5, 10, 11)).containsExactly(matchingEntity);
    }

    @Test
    void deleteByExternalReferencesAndCommodityNames(){
        CommodityEntity entity1 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_1)
                .build())
            .build();
        underTest.save(entity1);
        CommodityEntity entity2 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_2)
                .externalReference(EXTERNAL_REFERENCE_1)
                .build())
            .build();
        underTest.save(entity2);
        CommodityEntity entity3 = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(COMMODITY_NAME_1)
                .externalReference(EXTERNAL_REFERENCE_2)
                .build())
            .build();
        underTest.save(entity3);

        underTest.deleteByExternalReferencesAndCommodityNames(List.of(EXTERNAL_REFERENCE_1), List.of(COMMODITY_NAME_1));

        assertThat(underTest.findAll()).containsExactly(entity2, entity3);
    }
}