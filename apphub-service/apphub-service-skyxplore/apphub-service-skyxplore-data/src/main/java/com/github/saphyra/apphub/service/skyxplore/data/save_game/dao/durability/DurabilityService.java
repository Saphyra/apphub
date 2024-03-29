package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
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
public class DurabilityService implements GameItemService {
    private final DurabilityDao dao;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        dao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.DURABILITY;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<DurabilityModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof DurabilityModel)
            .map(gameItem -> (DurabilityModel) gameItem)
            .collect(Collectors.toList());

        dao.saveAll(models);
    }

    @Override
    public void deleteById(UUID id) {
        dao.deleteById(id);
    }

    @Override
    public List<DurabilityModel> loadPage(UUID gameId, Integer page, Integer itemsPerPage) {
        return dao.getPageByGameId(gameId, page, itemsPerPage);
    }
}
