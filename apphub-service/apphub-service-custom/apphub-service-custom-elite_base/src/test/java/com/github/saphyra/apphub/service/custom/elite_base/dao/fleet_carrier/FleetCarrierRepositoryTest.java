package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

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
class FleetCarrierRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String ID_3 = "id-3";
    private static final String CARRIER_ID_1 = "carrier-id-1";
    private static final String CARRIER_ID_2 = "carrier-id-2";
    private static final String CARRIER_ID_3 = "carrier-id-3";
    private static final Long MARKET_ID_1 = 324L;
    private static final Long MARKET_ID_2 = 325L;
    private static final Long MARKET_ID_3 = 326L;

    @Autowired
    private FleetCarrierRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByCarrierId() {
        FleetCarrierEntity entity = FleetCarrierEntity.builder()
            .id(ID_1)
            .carrierId(CARRIER_ID_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByCarrierId(CARRIER_ID_1)).contains(entity);
    }

    @Test
    void findByMarketId() {
        FleetCarrierEntity entity = FleetCarrierEntity.builder()
            .id(ID_1)
            .marketId(MARKET_ID_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByMarketId(MARKET_ID_1)).contains(entity);
    }

    @Test
    void getByCarrierIdOrMarketId_doubleMatch() {
        FleetCarrierEntity entity = FleetCarrierEntity.builder()
            .id(ID_1)
            .carrierId(CARRIER_ID_1)
            .marketId(MARKET_ID_1)
            .build();
        underTest.save(entity);
        FleetCarrierEntity entity2 = FleetCarrierEntity.builder()
            .id(ID_2)
            .carrierId(CARRIER_ID_2)
            .marketId(MARKET_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByCarrierIdOrMarketId(CARRIER_ID_1, MARKET_ID_1)).containsExactly(entity);
    }

    @Test
    void getByCarrierIdOrMarketId_carrierIdMatch() {
        FleetCarrierEntity entity = FleetCarrierEntity.builder()
            .id(ID_1)
            .carrierId(CARRIER_ID_1)
            .marketId(MARKET_ID_2)
            .build();
        underTest.save(entity);
        FleetCarrierEntity entity2 = FleetCarrierEntity.builder()
            .id(ID_2)
            .carrierId(CARRIER_ID_2)
            .marketId(MARKET_ID_3)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByCarrierIdOrMarketId(CARRIER_ID_1, MARKET_ID_1)).containsExactly(entity);
    }

    @Test
    void getByCarrierIdOrMarketId_marketIdMatch() {
        FleetCarrierEntity entity = FleetCarrierEntity.builder()
            .id(ID_1)
            .carrierId(CARRIER_ID_2)
            .marketId(MARKET_ID_1)
            .build();
        underTest.save(entity);
        FleetCarrierEntity entity2 = FleetCarrierEntity.builder()
            .id(ID_2)
            .carrierId(CARRIER_ID_3)
            .marketId(MARKET_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByCarrierIdOrMarketId(CARRIER_ID_1, MARKET_ID_1)).containsExactly(entity);
    }

    @Test
    void clearMarketId_doNotDelete() {
        FleetCarrierEntity entity1 = FleetCarrierEntity.builder()
            .id(ID_1)
            .marketId(MARKET_ID_1)
            .build();
        underTest.save(entity1);
        FleetCarrierEntity entity2 = FleetCarrierEntity.builder()
            .id(ID_2)
            .marketId(MARKET_ID_2)
            .build();
        underTest.save(entity2);

        underTest.clearMarketId(ID_1, MARKET_ID_1);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    void clearMarketId_delete() {
        FleetCarrierEntity entity1 = FleetCarrierEntity.builder()
            .id(ID_1)
            .marketId(MARKET_ID_1)
            .build();
        underTest.save(entity1);

        underTest.clearMarketId(ID_2, MARKET_ID_1);

        assertThat(underTest.findById(ID_1))
            .isNotEmpty()
            .get()
            .returns(null, FleetCarrierEntity::getMarketId);
    }
}