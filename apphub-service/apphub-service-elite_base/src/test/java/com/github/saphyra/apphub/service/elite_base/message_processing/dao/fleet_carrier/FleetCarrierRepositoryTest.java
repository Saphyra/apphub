package com.github.saphyra.apphub.service.elite_base.message_processing.dao.fleet_carrier;

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
    private static final String CARRIER_ID_1 = "carrier-id-1";
    private static final Long MARKET_ID_1 = 324L;

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
}