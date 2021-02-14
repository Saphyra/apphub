package com.github.saphyra.apphub.service.skyxplore.data.save_game.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
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
public class StoredResourceSaverService implements GameItemSaver {
    private final StoredResourceDao storedResourceDao;
    private final StoredResourceModelValidator storedResourceModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        storedResourceDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.STORED_RESOURCE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<StoredResourceModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof StoredResourceModel)
            .map(gameItem -> (StoredResourceModel) gameItem)
            .peek(storedResourceModelValidator::validate)
            .collect(Collectors.toList());

        storedResourceDao.saveAll(models);
    }
}
