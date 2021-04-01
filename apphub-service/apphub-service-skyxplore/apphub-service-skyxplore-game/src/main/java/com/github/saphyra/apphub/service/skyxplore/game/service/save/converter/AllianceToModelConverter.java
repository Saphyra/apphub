package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllianceToModelConverter {
    public AllianceModel convert(Alliance alliance, Game game) {
        AllianceModel model = new AllianceModel();
        model.setId(alliance.getAllianceId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.ALLIANCE);
        model.setName(alliance.getAllianceName());
        return model;
    }
}
