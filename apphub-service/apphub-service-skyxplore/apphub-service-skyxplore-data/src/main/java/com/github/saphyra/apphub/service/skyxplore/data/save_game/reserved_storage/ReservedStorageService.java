package com.github.saphyra.apphub.service.skyxplore.data.save_game.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservedStorageService implements GameItemService {
    private final ReservedStorageDao reservedStorageDao;
    private final ReservedStorageModelValidator reservedStorageModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        reservedStorageDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.RESERVED_STORAGE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<ReservedStorageModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof ReservedStorageModel)
            .map(gameItem -> (ReservedStorageModel) gameItem)
            .peek(reservedStorageModelValidator::validate)
            .collect(Collectors.toList());

        reservedStorageDao.saveAll(models);
    }
}
