package com.github.saphyra.apphub.service.skyxplore.data.save_game.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PriorityModelSaverService implements GameItemSaver {
    private final PriorityDao priorityDao;
    private final PriorityModelValidator priorityModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        priorityDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.PRIORITY;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<PriorityModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof PriorityModel)
            .map(gameItem -> (PriorityModel) gameItem)
            .peek(priorityModelValidator::validate)
            .collect(Collectors.toList());

        priorityDao.saveAll(models);
    }
}
