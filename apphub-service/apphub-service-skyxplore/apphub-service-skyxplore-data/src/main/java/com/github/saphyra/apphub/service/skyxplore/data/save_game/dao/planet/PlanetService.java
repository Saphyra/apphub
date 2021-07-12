package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
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
public class PlanetService implements GameItemService {
    private final PlanetDao planetDao;
    private final PlanetModelValidator planetModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        planetDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.PLANET;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<PlanetModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof PlanetModel)
            .map(gameItem -> (PlanetModel) gameItem)
            .peek(planetModelValidator::validate)
            .collect(Collectors.toList());


        planetDao.saveAll(models);
    }

    @Override
    public Optional<PlanetModel> findById(UUID id) {
        return planetDao.findById(id);
    }

    @Override
    public List<PlanetModel> getByParent(UUID parent) {
        return planetDao.getBySolarSystemId(parent);
    }
}
