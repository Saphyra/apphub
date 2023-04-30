package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings.alliance;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class AllianceFactory {
    private final IdGenerator idGenerator;

    Alliance create(int currentNumberOfAlliances) {
        return Alliance.builder()
            .allianceId(idGenerator.randomUuid())
            .allianceName(String.valueOf(currentNumberOfAlliances + 1))
            .build();
    }
}
