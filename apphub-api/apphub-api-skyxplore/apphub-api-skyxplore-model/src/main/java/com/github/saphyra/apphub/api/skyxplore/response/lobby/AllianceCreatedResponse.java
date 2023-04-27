package com.github.saphyra.apphub.api.skyxplore.response.lobby;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
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
