package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FleetCarrierDaoTest {
    private static final String CARRIER_ID = "carrier-od";
    private static final Long MARKET_ID = 243L;
    @Mock
    private FleetCarrierConverter converter;

    @Mock
    private FleetCarrierRepository repository;

    @InjectMocks
    private FleetCarrierDao underTest;

    @Mock
    private FleetCarrier domain;

    @Mock
    private FleetCarrierEntity entity;

    @Test
    void findByCarrierId() {
        given(repository.findByCarrierId(CARRIER_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByCarrierId(CARRIER_ID)).contains(domain);
    }

    @Test
    void findByMarketId() {
        given(repository.findByMarketId(MARKET_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByMarketId(MARKET_ID)).contains(domain);
    }
}