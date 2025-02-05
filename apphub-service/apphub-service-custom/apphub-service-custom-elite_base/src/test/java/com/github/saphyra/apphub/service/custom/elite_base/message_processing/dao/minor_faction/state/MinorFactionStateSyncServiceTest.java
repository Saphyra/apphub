package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.state;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MinorFactionStateSyncServiceTest {
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();

    @Mock
    private MinorFactionStateDao minorFactionStateDao;

    @InjectMocks
    private MinorFactionStateSyncService underTest;

    @Mock
    private MinorFactionState activeState;

    @Mock
    private MinorFactionState pendingState;

    @Mock
    private MinorFactionState recoveringState;

    @Mock
    private MinorFactionState deprecatedState;

    @Test
    void sync() {
        given(minorFactionStateDao.getByMinorFactionId(MINOR_FACTION_ID)).willReturn(List.of(pendingState, recoveringState, deprecatedState));

        underTest.sync(MINOR_FACTION_ID, List.of(activeState), List.of(pendingState), List.of(recoveringState));

        then(minorFactionStateDao).should().saveAll(List.of(activeState));
        then(minorFactionStateDao).should().deleteAll(List.of(deprecatedState));
    }
}