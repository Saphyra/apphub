package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
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
public class SurfaceService implements GameItemService {
    private final SurfaceDao surfaceDao;
    private final SurfaceModelValidator surfaceModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        surfaceDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.SURFACE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<SurfaceModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof SurfaceModel)
            .map(gameItem -> (SurfaceModel) gameItem)
            .peek(surfaceModelValidator::validate)
            .collect(Collectors.toList());

        surfaceDao.saveAll(models);
    }

    @Override
    public Optional<SurfaceModel> findById(UUID id) {
        return surfaceDao.findById(id);
    }

    @Override
    public List<SurfaceModel> getByParent(UUID parent) {
        return surfaceDao.getByPlanetId(parent);
    }

    @Override
    public void deleteById(UUID id) {
        surfaceDao.deleteById(id);
    }
}
