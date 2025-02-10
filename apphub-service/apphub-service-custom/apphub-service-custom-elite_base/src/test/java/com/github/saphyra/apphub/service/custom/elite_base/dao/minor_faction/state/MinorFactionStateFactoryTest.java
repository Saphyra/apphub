package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionStateFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.StateStatus;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.FactionState;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MinorFactionStateFactoryTest {
    private static final Integer TREND = 234;
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();

    @InjectMocks
    private MinorFactionStateFactory underTest;

    @Test
    void create() {
        FactionState state = new FactionState("CivilWar", TREND);

        CustomAssertions.singleListAssertThat(underTest.create(MINOR_FACTION_ID, StateStatus.ACTIVE, List.of(state)))
            .returns(MINOR_FACTION_ID, MinorFactionState::getMinorFactionId)
            .returns(StateStatus.ACTIVE, MinorFactionState::getStatus)
            .returns(FactionStateEnum.CIVIL_WAR, MinorFactionState::getState)
            .returns(TREND, MinorFactionState::getTrend);
    }
}