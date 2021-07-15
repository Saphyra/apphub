package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SystemConnectionLoader {
    private final GameItemLoader gameItemLoader;
    private final LineLoader lineLoader;

    public List<SystemConnection> load(UUID gameId) {
        List<SystemConnectionModel> models = gameItemLoader.loadChildren(gameId, GameItemType.SYSTEM_CONNECTION, SystemConnectionModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private SystemConnection convert(SystemConnectionModel model) {
        return SystemConnection.builder()
            .systemConnectionId(model.getId())
            .line(lineLoader.loadOne(model.getId()))
            .build();
    }
}
