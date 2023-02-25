package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructionToModelConverter {
    public DeconstructionModel convert(Deconstruction deconstruction, UUID gameId) {
        DeconstructionModel model = new DeconstructionModel();
        model.setId(deconstruction.getDeconstructionId());
        model.setGameId(gameId);
        model.setType(GameItemType.DECONSTRUCTION);
        model.setExternalReference(deconstruction.getExternalReference());
        model.setCurrentWorkPoints(deconstruction.getCurrentWorkPoints());
        model.setPriority(deconstruction.getPriority());
        return model;
    }
}
