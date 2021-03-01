package com.github.saphyra.apphub.service.skyxplore.data.save_game.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillSaverService implements GameItemSaver {
    private final SkillDao skillDao;
    private final SkillModelValidator skillModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
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
}
