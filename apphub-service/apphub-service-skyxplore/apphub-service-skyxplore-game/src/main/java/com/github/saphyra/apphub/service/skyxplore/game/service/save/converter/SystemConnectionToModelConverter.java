package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SystemConnectionToModelConverter {
    public List<SystemConnectionModel> convert(List<SystemConnection> connections, Game game) {
        return connections.stream()
            .map(systemConnection -> convert(systemConnection, game))
            .collect(Collectors.toList());
    }

    public SystemConnectionModel convert(SystemConnection connection, Game game) {
        SystemConnectionModel model = new SystemConnectionModel();
        model.setId(connection.getSystemConnectionId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.SYSTEM_CONNECTION);
        model.setLine(connection.getLine());
        return model;
    }
}
