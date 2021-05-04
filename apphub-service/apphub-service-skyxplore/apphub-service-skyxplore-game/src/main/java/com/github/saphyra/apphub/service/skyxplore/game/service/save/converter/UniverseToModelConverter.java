package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UniverseToModelConverter {
    private final SystemConnectionToModelConverter connectionConverter;
    private final SolarSystemToModelConverter solarSystemConverter;

    public List<GameItem> convertDeep(Universe universe, Game game) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(universe, game));
        result.addAll(connectionConverter.convert(universe.getConnections(), game));
        result.addAll(solarSystemConverter.convertDeep(universe.getSystems().values(), game));
        return result;
    }

    private UniverseModel convert(Universe universe, Game game) {
        UniverseModel model = new UniverseModel();
        model.setGameId(game.getGameId());
        model.setType(GameItemType.UNIVERSE);
        model.setSize(universe.getSize());
        return model;
    }
}
