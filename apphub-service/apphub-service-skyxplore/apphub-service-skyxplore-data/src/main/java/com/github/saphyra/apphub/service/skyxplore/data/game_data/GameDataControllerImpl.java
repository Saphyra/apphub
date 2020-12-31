package com.github.saphyra.apphub.service.skyxplore.data.game_data;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreGameDataController;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class GameDataControllerImpl implements SkyXploreGameDataController {
    private final Map<String, ? extends GameDataItem> items;

    public GameDataControllerImpl(List<AbstractDataService<?, ? extends GameDataItem>> dataServices) {
        this.items = dataServices.stream()
            .flatMap(dataService -> dataService.values().stream())
            .collect(Collectors.toMap(GameDataItem::getId, Function.identity()));
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    public Object getGameData(String dataId) {
        log.info("Querying gameData item with id {}", dataId);
        return Optional.ofNullable(items.get(dataId))
            .orElseThrow(() -> new RuntimeException("GameData item not found with id " + dataId));
    }
}
