package com.github.saphyra.apphub.service.skyxplore.game.domain.data.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllianceConverter {
    public AllianceModel toModel(Alliance alliance, Game game) {
        AllianceModel model = new AllianceModel();
        model.setId(alliance.getAllianceId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.ALLIANCE);
        model.setName(alliance.getAllianceName());
        return model;
    }
}
