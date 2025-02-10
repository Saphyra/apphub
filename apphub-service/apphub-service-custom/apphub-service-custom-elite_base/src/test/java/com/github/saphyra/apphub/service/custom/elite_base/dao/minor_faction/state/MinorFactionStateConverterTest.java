package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionStateConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionStateEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.StateStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MinorFactionStateConverterTest {
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();
    private static final Integer TREND = 23;
    private static final String MINOR_FACTION_ID_STRING = "minor-faction-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MinorFactionStateConverter underTest;

    @Test
    void convertDomain() {
        MinorFactionState domain = MinorFactionState.builder()
            .minorFactionId(MINOR_FACTION_ID)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .trend(TREND)
            .build();

        given(uuidConverter.convertDomain(MINOR_FACTION_ID)).willReturn(MINOR_FACTION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(MINOR_FACTION_ID_STRING, MinorFactionStateEntity::getMinorFactionId)
            .returns(StateStatus.ACTIVE, MinorFactionStateEntity::getStatus)
            .returns(FactionStateEnum.BLIGHT, MinorFactionStateEntity::getState)
            .returns(TREND, MinorFactionStateEntity::getTrend);
    }

    @Test
    void convertEntity() {
        MinorFactionStateEntity domain = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_STRING)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .trend(TREND)
            .build();

        given(uuidConverter.convertEntity(MINOR_FACTION_ID_STRING)).willReturn(MINOR_FACTION_ID);

        assertThat(underTest.convertEntity(domain))
            .returns(MINOR_FACTION_ID, MinorFactionState::getMinorFactionId)
            .returns(StateStatus.ACTIVE, MinorFactionState::getStatus)
            .returns(FactionStateEnum.BLIGHT, MinorFactionState::getState)
            .returns(TREND, MinorFactionState::getTrend);
    }
}