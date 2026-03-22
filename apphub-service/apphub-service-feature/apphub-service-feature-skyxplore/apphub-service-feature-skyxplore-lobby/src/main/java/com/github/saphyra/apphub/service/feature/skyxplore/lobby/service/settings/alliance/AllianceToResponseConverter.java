package com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.settings.alliance;

import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.Alliance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AllianceToResponseConverter {
     AllianceResponse convertToResponse(Alliance alliance) {
        return AllianceResponse.builder()
            .allianceId(alliance.getAllianceId())
            .allianceName(alliance.getAllianceName())
            .build();
    }
}
