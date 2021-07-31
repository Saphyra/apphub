package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
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
public class CitizenService implements GameItemService {
    private final CitizenDao citizenDao;
    private final CitizenModelValidator citizenModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        citizenDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.CITIZEN;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<CitizenModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof CitizenModel)
            .map(gameItem -> (CitizenModel) gameItem)
            .peek(citizenModelValidator::validate)
            .collect(Collectors.toList());

        citizenDao.saveAll(models);
    }

    @Override
    public Optional<CitizenModel> findById(UUID id) {
        return citizenDao.findById(id);
    }

    @Override
    public List<CitizenModel> getByParent(UUID parent) {
        return citizenDao.getByLocation(parent);
    }

    @Override
    public void deleteById(UUID id) {
        citizenDao.deleteById(id);
    }
}
