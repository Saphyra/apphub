package com.github.saphyra.apphub.service.skyxplore.game.domain.data.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.alliance.Alliance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerConverter {
    public PlayerModel toModel(Player player, Game game) {
        PlayerModel model = new PlayerModel();
        model.setId(player.getPlayerId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.PLAYER);
        model.setUserId(player.getUserId());
        model.setUsername(player.getPlayerName());
        model.setAi(player.isAi());
        UUID allianceId = game.getAlliances()
            .values()
            .stream()
            .filter(alliance -> alliance.getMembers().containsKey(player.getUserId()))
            .findFirst()
            .map(Alliance::getAllianceId)
            .orElse(null);
        model.setAllianceId(allianceId);
        return model;
    }
}
