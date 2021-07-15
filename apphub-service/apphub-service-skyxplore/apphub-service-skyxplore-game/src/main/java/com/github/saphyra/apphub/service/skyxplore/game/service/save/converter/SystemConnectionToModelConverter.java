package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemConnectionToModelConverter {
    public List<GameItem> convert(List<SystemConnection> connections, Game game) {
        return connections.stream()
            .flatMap(systemConnection -> convert(systemConnection, game))
            .collect(Collectors.toList());
    }

    private Stream<GameItem> convert(SystemConnection connection, Game game) {
        SystemConnectionModel model = new SystemConnectionModel();
        model.setId(connection.getSystemConnectionId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.SYSTEM_CONNECTION);
        return Stream.of(model, connection.getLine().getModel(), connection.getLine().getA(), connection.getLine().getB());
    }
}
