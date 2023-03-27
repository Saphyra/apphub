package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AiFactory {
    private final PlayerFactory playerFactory;

    public List<Player> generateAis(SkyXploreGameCreationRequest request) {
        return request.getAis()
            .stream()
            .map(playerFactory::createAi)
            .toList();
    }
}
