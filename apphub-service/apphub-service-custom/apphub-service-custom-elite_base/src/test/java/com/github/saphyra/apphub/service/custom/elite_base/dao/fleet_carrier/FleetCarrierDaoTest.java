package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FleetCarrierDaoTest {
    private static final String CARRIER_ID = "carrier-od";
    private static final Long MARKET_ID = 243L;
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";

    @Mock
    private UuidConverter uuidConverter;

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
    void findByMarketId() {
        given(repository.findByMarketId(MARKET_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByMarketId(MARKET_ID)).contains(domain);
    }

    @Test
    void findAllById() {
        given(uuidConverter.convertDomain(List.of(ID))).willReturn(List.of(ID_STRING));
        given(repository.findAllById(List.of(ID_STRING))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.findAllById(List.of(ID))).containsExactly(domain);
    }
}