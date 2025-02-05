package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class FleetCarrierConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String CARRIER_ID = "carrier-id";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String CARRIER_NAME = "carrier-name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long MARKET_ID = 3425L;
    private static final String ID_STRING = "id";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @InjectMocks
    private FleetCarrierConverter underTest;

    @Test
    void convertDomain() {
        FleetCarrier domain = FleetCarrier.builder()
            .id(ID)
            .carrierId(CARRIER_ID)
            .lastUpdate(LAST_UPDATE)
            .carrierName(CARRIER_NAME)
            .starSystemId(STAR_SYSTEM_ID)
            .dockingAccess(FleetCarrierDockingAccess.NONE)
            .marketId(MARKET_ID)
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, FleetCarrierEntity::getId)
            .returns(CARRIER_ID, FleetCarrierEntity::getCarrierId)
            .returns(LAST_UPDATE_STRING, FleetCarrierEntity::getLastUpdate)
            .returns(CARRIER_NAME, FleetCarrierEntity::getCarrierName)
            .returns(STAR_SYSTEM_ID_STRING, FleetCarrierEntity::getStarSystemId)
            .returns(FleetCarrierDockingAccess.NONE, FleetCarrierEntity::getDockingAccess)
            .returns(MARKET_ID, FleetCarrierEntity::getMarketId);
    }

    @Test
    void convertEntity() {
        FleetCarrierEntity domain = FleetCarrierEntity.builder()
            .id(ID_STRING)
            .carrierId(CARRIER_ID)
            .lastUpdate(LAST_UPDATE_STRING)
            .carrierName(CARRIER_NAME)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .dockingAccess(FleetCarrierDockingAccess.NONE)
            .marketId(MARKET_ID)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);

        assertThat(underTest.convertEntity(domain))
            .returns(ID, FleetCarrier::getId)
            .returns(CARRIER_ID, FleetCarrier::getCarrierId)
            .returns(LAST_UPDATE, FleetCarrier::getLastUpdate)
            .returns(CARRIER_NAME, FleetCarrier::getCarrierName)
            .returns(STAR_SYSTEM_ID, FleetCarrier::getStarSystemId)
            .returns(FleetCarrierDockingAccess.NONE, FleetCarrier::getDockingAccess)
            .returns(MARKET_ID, FleetCarrier::getMarketId);
    }
}