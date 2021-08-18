package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
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
public class SolarSystemService implements GameItemService {
    private final SolarSystemDao solarSystemDao;
    private final SolarSystemModelValidator solarSystemModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        solarSystemDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.SOLAR_SYSTEM;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<SolarSystemModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof SolarSystemModel)
            .map(gameItem -> (SolarSystemModel) gameItem)
            .peek(solarSystemModelValidator::validate)
            .collect(Collectors.toList());

        solarSystemDao.saveAll(models);
    }

    @Override
    public Optional<SolarSystemModel> findById(UUID id) {
        return solarSystemDao.findById(id);
    }

    @Override
    public List<SolarSystemModel> getByParent(UUID parent) {
        return solarSystemDao.getByGameId(parent);
    }

    @Override
    public void deleteById(UUID id) {
        solarSystemDao.deleteById(id);
    }
}
