package com.github.saphyra.apphub.api.skyxplore.response.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameViewForLobbyCreation {
    private UUID hostAllianceId;
    private String name;
    private List<AllianceModel> alliances;
    private List<PlayerModel> players;
}
