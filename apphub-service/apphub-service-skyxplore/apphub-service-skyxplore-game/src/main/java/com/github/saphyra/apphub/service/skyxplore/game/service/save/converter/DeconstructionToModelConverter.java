package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructionToModelConverter {
    public List<DeconstructionModel> convert(UUID gameId, Collection<Deconstruction> deconstructions) {
        return deconstructions.stream()
            .map(deconstruction -> convert(gameId, deconstruction))
            .collect(Collectors.toList());
    }

    public DeconstructionModel convert(UUID gameId, Deconstruction deconstruction) {
        DeconstructionModel model = new DeconstructionModel();
        model.setId(deconstruction.getDeconstructionId());
        model.setGameId(gameId);
        model.setType(GameItemType.DECONSTRUCTION);
        model.setExternalReference(deconstruction.getExternalReference());
        model.setLocation(deconstruction.getLocation());
        model.setCurrentWorkPoints(deconstruction.getCurrentWorkPoints());
        model.setPriority(deconstruction.getPriority());
        return model;
    }
}
