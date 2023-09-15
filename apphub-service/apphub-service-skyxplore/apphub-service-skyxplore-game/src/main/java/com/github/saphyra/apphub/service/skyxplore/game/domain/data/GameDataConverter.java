package com.github.saphyra.apphub.service.skyxplore.game.domain.data;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDataConverter {
    private final List<GameDataToModelConverter> converters;

    public List<GameItem> convert(UUID gameId, GameData data) {

        return converters.stream()
            .flatMap(gameDataToModelConverter -> gameDataToModelConverter.convert(gameId, data).stream())
            .collect(Collectors.toList());
    }
}
