package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
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
public class ConvoyService implements GameItemService {
    private final ConvoyDao dao;

    @Override
    public GameItemType getType() {
        return GameItemType.CONVOY;
    }

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        dao.deleteByGameId(gameId);
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<ConvoyModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof ConvoyModel)
            .map(gameItem -> (ConvoyModel) gameItem)
            .collect(Collectors.toList());

        dao.saveAll(models);
    }

    @Override
    public void deleteById(UUID id) {
        dao.deleteById(id);
    }

    @Override
    public List<ConvoyModel> loadPage(UUID gameId, Integer page, Integer itemsPerPage) {
        return dao.getPageByGameId(gameId, page, itemsPerPage);
    }
}
