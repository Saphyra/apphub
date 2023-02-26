package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
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
public class DeconstructionService implements GameItemService {
    private final DeconstructionDao dao;
    private final DeconstructionModelValidator validator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        dao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.DECONSTRUCTION;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<DeconstructionModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof DeconstructionModel)
            .map(gameItem -> (DeconstructionModel) gameItem)
            .peek(validator::validate)
            .collect(Collectors.toList());

        dao.saveAll(models);
    }

    @Override
    public Optional<DeconstructionModel> findById(UUID id) {
        return dao.findById(id);
    }

    @Override
    public List<DeconstructionModel> getByParent(UUID parent) {
        return dao.getByExternalReference(parent);
    }

    @Override
    public void deleteById(UUID id) {
        dao.deleteById(id);
    }
}
