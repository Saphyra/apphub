package com.github.saphyra.apphub.service.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.dao.minor_faction.state.MinorFactionStateFactory;
import com.github.saphyra.apphub.service.elite_base.dao.minor_faction.state.StateStatus;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.FactionState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MinorFactionFactory {
    private final IdGenerator idGenerator;
    private final MinorFactionStateFactory minorFactionStateFactory;

    public MinorFaction create(
        LocalDateTime timestamp,
        String factionName,
        FactionStateEnum economicState,
        Double influence,
        Allegiance allegiance,
        List<FactionState> activeStates,
        List<FactionState> pendingStates,
        List<FactionState> recoveringStates
    ) {
        UUID id = idGenerator.randomUuid();
        return MinorFaction.builder()
            .id(id)
            .lastUpdate(timestamp)
            .factionName(factionName)
            .state(economicState)
            .influence(influence)
            .allegiance(allegiance)
            .activeStates(LazyLoadedField.loaded(minorFactionStateFactory.create(id, StateStatus.ACTIVE, activeStates)))
            .pendingStates(LazyLoadedField.loaded(minorFactionStateFactory.create(id, StateStatus.PENDING, pendingStates)))
            .recoveringStates(LazyLoadedField.loaded(minorFactionStateFactory.create(id, StateStatus.RECOVERING, recoveringStates)))
            .build();
    }
}
