package com.github.saphyra.apphub.service.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.service.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.FactionState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MinorFactionStateFactory {
    public List<MinorFactionState> create(UUID minorFactorId, StateStatus status, List<FactionState> states) {
        return states.stream()
            .map(factionState -> create(minorFactorId, status, factionState))
            .toList();
    }

    private MinorFactionState create(UUID minorFactorId, StateStatus status, FactionState factionState) {
        return MinorFactionState.builder()
            .minorFactionId(minorFactorId)
            .status(status)
            .state(FactionStateEnum.parse(factionState.getState()))
            .trend(factionState.getTrend())
            .build();
    }
}
