package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDockingAccess;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FleetCarrierFactoryTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String CARRIER_ID = "carrier-id";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String CARRIER_NAME = "carrier-name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long MARKET_ID = 2354L;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private FleetCarrierFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ID);

        assertThat(underTest.create(CARRIER_ID, LAST_UPDATE, CARRIER_NAME, STAR_SYSTEM_ID, FleetCarrierDockingAccess.NONE, MARKET_ID))
            .returns(ID, FleetCarrier::getId)
            .returns(CARRIER_ID, FleetCarrier::getCarrierId)
            .returns(LAST_UPDATE, FleetCarrier::getLastUpdate)
            .returns(CARRIER_NAME, FleetCarrier::getCarrierName)
            .returns(STAR_SYSTEM_ID, FleetCarrier::getStarSystemId)
            .returns(FleetCarrierDockingAccess.NONE, FleetCarrier::getDockingAccess)
            .returns(MARKET_ID, FleetCarrier::getMarketId);
    }
}