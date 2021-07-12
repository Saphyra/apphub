package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.allocated_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocatedResourceService implements GameItemService {
    private final AllocatedResourceDao allocatedResourceDao;
    private final AllocatedResourceModelValidator allocatedResourceModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        allocatedResourceDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.ALLOCATED_RESOURCE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<AllocatedResourceModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof AllocatedResourceModel)
            .map(gameItem -> (AllocatedResourceModel) gameItem)
            .peek(allocatedResourceModelValidator::validate)
            .collect(Collectors.toList());

        allocatedResourceDao.saveAll(models);
    }

    @Override
    public Optional<AllocatedResourceModel> findById(UUID id) {
        return allocatedResourceDao.findById(id);
    }

    @Override
    public List<AllocatedResourceModel> getByParent(UUID parent) {
        return allocatedResourceDao.getByLocation(parent);
    }
}
