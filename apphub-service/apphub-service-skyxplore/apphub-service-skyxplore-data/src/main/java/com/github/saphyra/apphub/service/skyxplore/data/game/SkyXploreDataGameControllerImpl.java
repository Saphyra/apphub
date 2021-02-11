package com.github.saphyra.apphub.service.skyxplore.data.game;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreDataGameController;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreDataGameControllerImpl implements SkyXploreDataGameController {
    @Override
    //TODO unit test
    //TODO int test
    public void saveGameData(List<GameItem> items) {
        log.info("saveGameData request arrived with size {} and types {}", items.size(), items.stream().map(GameItem::getType).map(Enum::name).collect(Collectors.joining(", ")));
        //TODO implement
    }
}
