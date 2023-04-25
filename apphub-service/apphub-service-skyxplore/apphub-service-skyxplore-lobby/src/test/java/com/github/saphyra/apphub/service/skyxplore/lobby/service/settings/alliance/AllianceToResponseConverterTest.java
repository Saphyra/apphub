package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings.alliance;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AllianceToResponseConverterTest {
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";

    private final AllianceToResponseConverter underTest = new AllianceToResponseConverter();

    @Test
    void convertToResponse() {
        Alliance alliance = Alliance.builder()
            .allianceId(ALLIANCE_ID)
            .allianceName(ALLIANCE_NAME)
            .build();

        AllianceResponse result = underTest.convertToResponse(alliance);

        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getAllianceName()).isEqualTo(ALLIANCE_NAME);
    }
}