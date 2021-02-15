package com.github.saphyra.apphub.service.skyxplore.data.save_game.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class AllianceSaverService implements GameItemSaver {
    private final AllianceDao allianceDao;
    private final AllianceModelValidator allianceModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        allianceDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.ALLIANCE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<AllianceModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof AllianceModel)
            .map(gameItem -> (AllianceModel) gameItem)
            .peek(allianceModelValidator::validate)
            .collect(Collectors.toList());


        allianceDao.saveAll(models);
    }
}
