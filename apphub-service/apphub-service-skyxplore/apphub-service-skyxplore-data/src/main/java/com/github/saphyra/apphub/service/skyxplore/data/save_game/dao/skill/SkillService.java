package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
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
public class SkillService implements GameItemService {
    private final SkillDao skillDao;
    private final SkillModelValidator skillModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        skillDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.SKILL;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<SkillModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof SkillModel)
            .map(gameItem -> (SkillModel) gameItem)
            .peek(skillModelValidator::validate)
            .collect(Collectors.toList());

        skillDao.saveAll(models);
    }

    @Override
    public Optional<SkillModel> findById(UUID id) {
        return skillDao.findById(id);
    }

    @Override
    public List<SkillModel> getByParent(UUID parent) {
        return skillDao.getByCitizenId(parent);
    }

    @Override
    public void deleteById(UUID id) {
        skillDao.deleteById(id);
    }
}
