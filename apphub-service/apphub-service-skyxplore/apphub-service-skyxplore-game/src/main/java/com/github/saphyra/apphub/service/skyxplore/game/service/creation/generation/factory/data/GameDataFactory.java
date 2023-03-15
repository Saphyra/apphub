package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GameDataFactory {
    public GameData create(Collection<Player> players, Collection<Alliance> values, SkyXploreGameCreationSettingsRequest settings) {
        return null;
    }
}
