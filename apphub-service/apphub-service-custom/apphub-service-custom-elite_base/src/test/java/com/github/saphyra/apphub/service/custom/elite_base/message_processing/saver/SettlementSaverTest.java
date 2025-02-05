package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement.Settlement;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement.SettlementDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement.SettlementFactory;
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
class SettlementSaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String SETTLEMENT_NAME = "settlement-name";
    private static final Long MARKET_ID = 3432L;
    private static final Double LONGITUDE = 23423.324;
    private static final Double LATITUDE = 24.234;

    @Mock
    private SettlementDao settlementDao;

    @Mock
    private SettlementFactory settlementFactory;

    @InjectMocks
    private SettlementSaver underTest;

    @Mock
    private Settlement settlement;

    @Test
    void nullStarSystemId() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, null, BODY_ID, SETTLEMENT_NAME, MARKET_ID, LONGITUDE, LATITUDE))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullSettlementName() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BODY_ID, null, MARKET_ID, LONGITUDE, LATITUDE))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void save_new() {
        given(settlementDao.findByStarSystemIdAndSettlementName(STAR_SYSTEM_ID, SETTLEMENT_NAME)).willReturn(Optional.empty());
        given(settlementFactory.create(LAST_UPDATE, STAR_SYSTEM_ID, BODY_ID, SETTLEMENT_NAME, MARKET_ID, LONGITUDE, LATITUDE)).willReturn(settlement);
        given(settlement.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BODY_ID, SETTLEMENT_NAME, MARKET_ID, LONGITUDE, LATITUDE);

        then(settlement).should(times(0)).setLastUpdate(any());
        then(settlement).should(times(0)).setBodyId(any());
        then(settlement).should(times(0)).setMarketId(any());
        then(settlement).should(times(0)).setLongitude(any());
        then(settlement).should(times(0)).setLatitude(any());
        then(settlementDao).should().save(settlement);
    }

    @Test
    void save_existing() {
        given(settlementDao.findByStarSystemIdAndSettlementName(STAR_SYSTEM_ID, SETTLEMENT_NAME)).willReturn(Optional.of(settlement));
        given(settlement.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));

        underTest.save(LAST_UPDATE, STAR_SYSTEM_ID, BODY_ID, SETTLEMENT_NAME, MARKET_ID, LONGITUDE, LATITUDE);

        then(settlement).should().setLastUpdate(LAST_UPDATE);
        then(settlement).should().setBodyId(BODY_ID);
        then(settlement).should().setMarketId(MARKET_ID);
        then(settlement).should().setLongitude(LONGITUDE);
        then(settlement).should().setLatitude(LATITUDE);
        then(settlementDao).should().save(settlement);
    }
}