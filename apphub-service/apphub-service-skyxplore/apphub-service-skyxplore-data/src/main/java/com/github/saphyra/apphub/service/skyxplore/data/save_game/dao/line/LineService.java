package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
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
public class LineService implements GameItemService {
    private final LineDao lineDao;
    private final LineModelValidator lineModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        lineDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.LINE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<LineModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof LineModel)
            .map(gameItem -> (LineModel) gameItem)
            .peek(lineModelValidator::validate)
            .collect(Collectors.toList());

        lineDao.saveAll(models);
    }

    @Override
    public Optional<LineModel> findById(UUID id) {
        return lineDao.findById(id);
    }

    @Override
    public List<LineModel> getByParent(UUID parent) {
        return lineDao.getByReferenceId(parent);
    }

    @Override
    public void deleteById(UUID id) {
        lineDao.deleteById(id);
    }
}
