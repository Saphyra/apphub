package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
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
public class BuildingAllocationService implements GameItemService {
    private final BuildingAllocationDao dao;

    @Override
    public GameItemType getType() {
        return GameItemType.BUILDING_ALLOCATION;
    }

    @Override
    public void deleteByGameId(UUID gameId) {
        dao.deleteByGameId(gameId);
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<BuildingAllocationModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof BuildingAllocationModel)
            .map(gameItem -> (BuildingAllocationModel) gameItem)
            .collect(Collectors.toList());

        dao.saveAll(models);
    }

    @Override
    public void deleteById(UUID buildingAllocationId) {
        dao.deleteById(buildingAllocationId);
    }

    @Override
    public List<BuildingAllocationModel> loadPage(UUID gameId, Integer page, Integer itemsPerPage) {
        return dao.getPageByGameId(gameId, page, itemsPerPage);
    }
}
