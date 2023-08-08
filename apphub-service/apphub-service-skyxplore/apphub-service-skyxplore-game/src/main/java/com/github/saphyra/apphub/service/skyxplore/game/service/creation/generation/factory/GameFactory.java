package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.alliance.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoopFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.GameDataFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player.AiFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player.PlayerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameFactory {
    private final AllianceFactory allianceFactory;
    private final IdGenerator idGenerator;
    private final GameDataFactory gameDataFactory;
    private final ChatFactory chatFactory;
    private final DateTimeUtil dateTimeUtil;
    private final PlayerFactory playerFactory;
    private final AiFactory aiFactory;
    private final EventLoopFactory eventLoopFactory;

    public Game create(SkyXploreGameCreationRequest request) {
        UUID gameId = idGenerator.randomUuid();

        Map<UUID, Player> players = playerFactory.create(request.getMembers());
        aiFactory.generateAis(request)
            .forEach(player -> players.put(player.getUserId(), player));
        Map<UUID, Alliance> alliances = allianceFactory.create(request.getAlliances(), request.getMembers(), players);

        Game result = Game.builder()
            .gameId(gameId)
            .host(request.getHost())
            .players(players)
            .alliances(alliances)
            .data(gameDataFactory.create(gameId, players.values(), request.getSettings()))
            .chat(chatFactory.create(request.getMembers()))
            .gameName(request.getGameName())
            .lastPlayed(dateTimeUtil.getCurrentDateTime())
            .eventLoop(eventLoopFactory.create())
            .markedForDeletion(false)
            .build();

        log.info("Game generated.");

        return result;
    }
}
