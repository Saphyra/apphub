package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PlayerLoader extends AbstractGameItemLoader<PlayerModel> {
    private final GameDataProxy gameDataProxy;

    public PlayerLoader(GameItemLoader gameItemLoader, GameDataProxy gameDataProxy) {
        super(gameItemLoader);
        this.gameDataProxy = gameDataProxy;
    }

    Map<UUID, Player> load(UUID gameId, List<UUID> members) {
        List<PlayerModel> players = getByGameId(gameId);
        return players.stream()
            .map(playerModel -> convert(playerModel, members))
            .collect(Collectors.toMap(Player::getUserId, Function.identity()));
    }

    private Player convert(PlayerModel model, List<UUID> members) {
        boolean ai = model.getAi() || isAi(model, members);
        return Player.builder()
            .playerId(model.getId())
            .userId(model.getUserId())
            .allianceId(model.getAllianceId())
            .playerName(model.getUsername())
            .ai(ai)
            .connected(ai)
            .build();
    }

    //Set players, who are not in the lobby to AI
    private boolean isAi(PlayerModel model, List<UUID> members) {
        if (members.contains(model.getUserId())) {
            return false;
        }

        model.setAi(true);
        gameDataProxy.saveItem(model);
        return true;
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.PLAYER;
    }

    @Override
    protected Class<PlayerModel[]> getClassArray() {
        return PlayerModel[].class;
    }
}
