package com.github.saphyra.apphub.service.skyxplore.data.save_game.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO unit test
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
    public void save(GameItem gameItem) {
        if (!(gameItem instanceof AllianceModel)) {
            throw new IllegalArgumentException("GameItem is not a " + getType() + ", it is " + gameItem.getType());
        }

        AllianceModel model = (AllianceModel) gameItem;
        allianceModelValidator.validate(model);

        allianceDao.save(model);
    }
}
