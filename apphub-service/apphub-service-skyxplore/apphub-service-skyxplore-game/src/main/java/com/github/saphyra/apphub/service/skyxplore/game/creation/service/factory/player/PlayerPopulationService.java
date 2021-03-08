package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.player;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerPopulationService {
    private final GameCreationProperties properties;
    private final IdGenerator idGenerator;
    private final Random random;
    private final PlayerFactory playerFactory;

    public Map<UUID, Player> populateGameWithPlayers(Set<UUID> userIds, int planetAmount, SkyXploreGameCreationSettingsRequest settings) {
        log.info("Generating players...");
        List<Player> players = new ArrayList<>();

        log.info("Generating real players...");
        List<String> usedPlayerNames = new ArrayList<>();

        userIds.stream()
            .map(userId -> playerFactory.create(userId, false, usedPlayerNames))
            .forEach(players::add);

        log.info("Generating aIs...");
        Stream.generate(idGenerator::randomUuid)
            .limit(planetAmount - userIds.size())
            .filter(uuid -> random.randInt(0, 100) < properties.getPlayer().getSpawnChance().get(settings.getAiPresence()))
            .map(uuid -> playerFactory.create(uuid, true, usedPlayerNames))
            .forEach(players::add);

        if (players.size() < 2) {
            log.info("There is only one player. Adding an AI...");
            players.add(playerFactory.create(idGenerator.randomUuid(), true, usedPlayerNames));
        }

        log.info("Players generated.");
        return players.stream()
            .collect(Collectors.toMap(Player::getUserId, Function.identity()));
    }
}
