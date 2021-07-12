package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LineModelFactory {
    public LineModel create(UUID lineId, UUID gameId, UUID referenceId, UUID a, UUID b) {
        LineModel model = new LineModel();
        model.setId(lineId);
        model.setGameId(gameId);
        model.setType(GameItemType.LINE);
        model.setReferenceId(referenceId);
        model.setA(a);
        model.setB(b);
        return model;
    }
}
