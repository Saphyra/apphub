package com.github.saphyra.apphub.service.elite_base.message_processing.dao.settlement;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
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
class SettlementFactoryTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String SETTLEMENT_NAME = "settlement-name";
    private static final Long MARKET_ID = 343L;
    private static final Double LONGITUDE = 3421.3421;
    private static final Double LATITUDE = 5645.456;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private SettlementFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ID);

        assertThat(underTest.create(LAST_UPDATE, STAR_SYSTEM_ID, BODY_ID, SETTLEMENT_NAME, MARKET_ID, LONGITUDE, LATITUDE))
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