package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiFactory {
    private final PlayerFactory playerFactory;
    private final AiCountCalculator aiCountCalculator;
    private final AllianceCounter allianceCounter;

    public List<Player> generateAis(SkyXploreGameCreationRequest request, Collection<Player> players) {
        List<String> usedPlayerNames = players.stream()
            .map(Player::getPlayerName)
            .collect(Collectors.toList());
        int allianceCount = allianceCounter.getAllianceCount(request.getMembers());
        return Stream.generate(() -> playerFactory.createAi(usedPlayerNames))
            .limit(aiCountCalculator.getAiCount(players.size(), request.getSettings().getAiPresence(), allianceCount))
            .collect(Collectors.toList());
    }
}
