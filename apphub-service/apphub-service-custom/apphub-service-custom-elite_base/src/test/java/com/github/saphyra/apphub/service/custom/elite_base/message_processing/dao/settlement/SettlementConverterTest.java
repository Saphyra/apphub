package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement;

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
class SettlementConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String SETTLEMENT_NAME = "settlement-name";
    private static final Long MARKET_ID = 324L;
    private static final Double LONGITUDE = 3243.345;
    private static final Double LATITUDE = 345436.454;
    private static final String ID_STRING = "id";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final String BODY_ID_STRING = "body-id";
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @InjectMocks
    private SettlementConverter underTest;

    @Test
    void convertDomain() {
        Settlement domain = Settlement.builder()
            .id(ID)
            .lastUpdate(LAST_UPDATE)
            .starSystemId(STAR_SYSTEM_ID)
            .bodyId(BODY_ID)
            .settlementName(SETTLEMENT_NAME)
            .marketId(MARKET_ID)
            .longitude(LONGITUDE)
            .latitude(LATITUDE)
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(uuidConverter.convertDomain(BODY_ID)).willReturn(BODY_ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, SettlementEntity::getId)
            .returns(LAST_UPDATE_STRING, SettlementEntity::getLastUpdate)
            .returns(STAR_SYSTEM_ID_STRING, SettlementEntity::getStarSystemId)
            .returns(BODY_ID_STRING, SettlementEntity::getBodyId)
            .returns(SETTLEMENT_NAME, SettlementEntity::getSettlementName)
            .returns(MARKET_ID, SettlementEntity::getMarketId)
            .returns(LONGITUDE, SettlementEntity::getLongitude)
            .returns(LATITUDE, SettlementEntity::getLatitude);
    }

    @Test
    void convertEntity() {
        SettlementEntity domain = SettlementEntity.builder()
            .id(ID_STRING)
            .lastUpdate(LAST_UPDATE_STRING)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .bodyId(BODY_ID_STRING)
            .settlementName(SETTLEMENT_NAME)
            .marketId(MARKET_ID)
            .longitude(LONGITUDE)
            .latitude(LATITUDE)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);
        given(uuidConverter.convertEntity(BODY_ID_STRING)).willReturn(BODY_ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);

        assertThat(underTest.convertEntity(domain))
            .returns(ID, Settlement::getId)
            .returns(LAST_UPDATE, Settlement::getLastUpdate)
            .returns(STAR_SYSTEM_ID, Settlement::getStarSystemId)
            .returns(BODY_ID, Settlement::getBodyId)
            .returns(SETTLEMENT_NAME, Settlement::getSettlementName)
            .returns(MARKET_ID, Settlement::getMarketId)
            .returns(LONGITUDE, Settlement::getLongitude)
            .returns(LATITUDE, Settlement::getLatitude);
    }
}