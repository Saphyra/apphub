package com.github.saphyra.apphub.service.feature.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FleetCarrierFactoryTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String CARRIER_ID = "carrier-id";
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

        assertThat(underTest.create(CARRIER_ID, CARRIER_NAME, STAR_SYSTEM_ID, FleetCarrierDockingAccess.NONE, MARKET_ID))
            .returns(ID, FleetCarrier::getId)
            .returns(CARRIER_ID, FleetCarrier::getCarrierId)
            .returns(CARRIER_NAME, FleetCarrier::getCarrierName)
            .returns(STAR_SYSTEM_ID, FleetCarrier::getStarSystemId)
            .returns(FleetCarrierDockingAccess.NONE, FleetCarrier::getDockingAccess)
            .returns(MARKET_ID, FleetCarrier::getMarketId);
    }
}