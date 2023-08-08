package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AllianceCreatedResponse {
    private AllianceResponse alliance;
    private LobbyMemberResponse member;
    private AiPlayer ai;
}
