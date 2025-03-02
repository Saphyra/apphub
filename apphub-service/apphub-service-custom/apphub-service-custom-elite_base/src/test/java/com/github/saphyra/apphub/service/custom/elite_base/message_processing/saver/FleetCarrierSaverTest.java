package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDockingAccess;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FleetCarrierSaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String CARRIER_NAME = "carrier-name";
    private static final Long MARKET_ID = 2432L;
    private static final String CARRIER_ID = "carrier-id";

    @Mock
    private FleetCarrierDao fleetCarrierDao;

    @Mock
    private FleetCarrierFactory fleetCarrierFactory;

    @InjectMocks
    private FleetCarrierSaver underTest;

    @Mock
    private FleetCarrier fleetCarrier;

    @Test
    void nullCarrierId() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, null, CARRIER_NAME, FleetCarrierDockingAccess.ALL, MARKET_ID))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void carrierNotFound() {
        given(fleetCarrierDao.findByCarrierId(CARRIER_ID)).willReturn(Optional.empty());
        given(fleetCarrierFactory.create(CARRIER_ID, LAST_UPDATE, CARRIER_NAME, STAR_SYSTEM_ID, FleetCarrierDockingAccess.ALL, MARKET_ID)).willReturn(fleetCarrier);
        given(fleetCarrier.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, CARRIER_ID, CARRIER_NAME, FleetCarrierDockingAccess.ALL, MARKET_ID)).isEqualTo(fleetCarrier);

        then(fleetCarrier).should(times(0)).setLastUpdate(any());
        then(fleetCarrier).should(times(0)).setStarSystemId(any());
        then(fleetCarrier).should(times(0)).setCarrierName(any());
        then(fleetCarrier).should(times(0)).setDockingAccess(any());
        then(fleetCarrier).should(times(0)).setMarketId(any());
        then(fleetCarrierDao).should().save(fleetCarrier);
    }

    @Test
    void carrierFound() {
        given(fleetCarrierDao.findByCarrierId(CARRIER_ID)).willReturn(Optional.of(fleetCarrier));
        given(fleetCarrier.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        given(fleetCarrier.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, CARRIER_ID, CARRIER_NAME, FleetCarrierDockingAccess.ALL, MARKET_ID)).isEqualTo(fleetCarrier);

        then(fleetCarrier).should().setLastUpdate(LAST_UPDATE);
        then(fleetCarrier).should().setStarSystemId(STAR_SYSTEM_ID);
        then(fleetCarrier).should().setCarrierName(CARRIER_NAME);
        then(fleetCarrier).should().setDockingAccess(FleetCarrierDockingAccess.ALL);
        then(fleetCarrier).should().setMarketId(MARKET_ID);
        then(fleetCarrierDao).should().save(fleetCarrier);
    }
}