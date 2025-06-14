package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoaderValidator {
    private static final List<GameItemType> UNLOADED_TYPES = List.of(
        GameItemType.GAME,
        GameItemType.LINE
    );

    private final List<AbstractGameItemLoader<?>> loaders;

    @PostConstruct
    void validate() {
        List<GameItemType> requiredTypes = Arrays.stream(GameItemType.values())
            .filter(gameItemType -> !UNLOADED_TYPES.contains(gameItemType))
            .toList();

        List<GameItemType> existingLoaders = loaders.stream()
            .map(AbstractGameItemLoader::getGameItemType)
            .toList();

        List<GameItemType> missingLoaders = requiredTypes.stream()
            .filter(gameItemType -> !existingLoaders.contains(gameItemType))
            .toList();

        if (!missingLoaders.isEmpty()) {
            throw new IllegalStateException("Missing GameItemLoaders: " + missingLoaders);
        }
    }
}
